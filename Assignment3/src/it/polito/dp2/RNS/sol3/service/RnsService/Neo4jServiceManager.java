package it.polito.dp2.RNS.sol3.service.RnsService;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.RNS.sol3.jaxb.neo4j.*;


import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RnsReaderFactory;
import it.polito.dp2.RNS.sol3.service.db.Neo4jDB;
import it.polito.dp2.RNS.sol3.service.db.RnsDB;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.PlaceReader;

public class Neo4jServiceManager {
	private static Neo4jServiceManager neo4jServiceManager = new Neo4jServiceManager();
	private  Client client;
	private static String serviceBaseUri = System.getProperty("it.polito.dp2.RNS.lab3.Neo4JURL");
	private JAXBContext jc;
	private javax.xml.validation.Validator validator;
	private Neo4jDB neo4jDB = Neo4jDB.getNeo4jDB();
	
	private Neo4jServiceManager ()  {
		this.client = ClientBuilder.newClient();
		if (serviceBaseUri == null)
			serviceBaseUri = "http://localhost:7474/db";
		
		// create validator that uses the DataTypes schema
    	/*SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
    	Schema schema;
		try {
			//schema = sf.newSchema(new File("xsd/Neo4jXML.xsd"));
			schema = sf.newSchema(new File("xsd/RnsSystem.xsd"));
			
			validator = schema.newValidator();
	    	//validator.setErrorHandler(new MyErrorHandler());
	    	
			// create JAXB context related to the classed generated from the DataTypes schema
	        jc = JAXBContext.newInstance("it.polito.dp2.RNS.sol3.service");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
	}
	
	public static Neo4jServiceManager getNeo4jServiceManager() {
		return neo4jServiceManager;
	}
	
	public void initPlaces(Set<PlaceReader> places){
		for (PlaceReader place : places){
			String placeId = place.getId();
			CreateNodeType node = new CreateNodeType();
			node.setId(placeId);
			//validate the node against schema
			//if (nodeValidation(node) == true){
				//if no error occurred in the validation send a request to neo4j
				Node responseNode = sendCreationNodeRequest(node);
				//validate the response object against schema
				//if (nodeValidation(responseNode) == true){
					//save in local DB
					//this.mapPlaceIDtoNode.put(placeId, responseNode);
					//save mapping url - placereader
					//this.mapUrlToPlace.put(responseNode.getSelf(),place);
				//}		
				
				//add the placeId - node tuple to the db
				if (neo4jDB.addNode(placeId, responseNode)== false){
					//error: already present in the db
				}
			//}
		}
		return;
	}
	
	public void initConnections(Set<ConnectionReader> Connections){
		for (ConnectionReader connection : Connections){
			String fromId = connection.getFrom().getId();
			String toId = connection.getTo().getId();
			Node fromNode = neo4jDB.getNodeByPlaceId(fromId);
			if (fromNode != null){
				Node toNode = neo4jDB.getNodeByPlaceId(toId);
				if (toNode != null){
					CreateRelationshipType relationship = new CreateRelationshipType();
					String fromUri = fromNode.getSelf();
					String toUri = toNode.getSelf();
					relationship.setTo(toUri);	
					relationship.setType("ConnectedTo");
					//validate the node against schema
					//if (nodeValidation(relationship) == true){
						//if no error occurred in the validation send a request to neo4j
						Relationship responseRelationship = sendCreationRelationshipRequest(fromUri,relationship);
						
						//validate the response object against schema
						//if (nodeValidation(responseRelationship) == true){
							//save in local DB
							//mapPlaceIDtoNode.put(placeId, responseNode);
						//}		
					//}
					
					
				}else{
					//erro to node is null
				}
			}else{
				//error from node is null
			}
			
		}
		return;
	}
	
	protected static UriBuilder getBaseURI() {
		return UriBuilder.fromUri(serviceBaseUri + "/data");
	}
	
	protected boolean nodeValidation(Object node){
		try {
	    	JAXBSource source = new JAXBSource(jc, node);
	    	validator.validate(source);
		} catch (org.xml.sax.SAXException se) {
		      System.out.println("Validation Failed");
		      // print error messages
		      Throwable t = se;
		      while (t!=null) {
			      String message = t.getMessage();
			      if (message!= null)
			    	  System.out.println(message);
			      t = t.getCause();
		      }
		      return false;
		} catch (IOException e) {
			System.out.println("Unexpected I/O Exception");
			return false;
		} catch (JAXBException e) {
			System.out.println("Unexpected JAXB Exception");
			return false;
		}
		return true;
	}
	
	//send a creation node to neo4j and return a nodeObject
	protected Node sendCreationNodeRequest(CreateNodeType node){
		// build the web target
		WebTarget target = client.target(getBaseURI()).path("node");
		Node responseNode;
		
		Response response = target
				   .request()
				   .accept(MediaType.APPLICATION_JSON)
				   .post(Entity.json(node));
		if (response.getStatus()!=201) {
			System.out.println("Error in remote operation: "+response.getStatus()+" "+response.getStatusInfo());
		}
		
		responseNode = response.readEntity(Node.class);
		return responseNode;
	}
	
	//send a relationship to neo4j
	protected Relationship sendCreationRelationshipRequest (String targetUri,CreateRelationshipType relationship){
		
		
		// build the web target
		WebTarget target = client.target(UriBuilder.fromUri(targetUri+"/relationships"));
		Relationship responseRelationship;
		
		Response response = target
				   .request()
				   .accept(MediaType.APPLICATION_JSON)
				   .post(Entity.json(relationship));
		if (response.getStatus()!=201) {
			System.out.println("Error in remote operation: "+response.getStatus()+" "+response.getStatusInfo());
		}
		
		responseRelationship = response.readEntity(Relationship.class);
		//System.out.println("relationship: from: "+responseRelationship.getStart()+" to : "+responseRelationship.getEnd()+"\n");
		return responseRelationship;
	}
	
	
	
	
}

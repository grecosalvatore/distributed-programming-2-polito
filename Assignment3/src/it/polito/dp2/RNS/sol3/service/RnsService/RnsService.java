package it.polito.dp2.RNS.sol3.service.RnsService;



import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.xml.sax.SAXException;

import it.polito.dp2.RNS.sol3.jaxb.ConnectedTo;
import it.polito.dp2.RNS.sol3.jaxb.ConnectedTo.To;
import it.polito.dp2.RNS.sol3.jaxb.Connection;
import it.polito.dp2.RNS.sol3.jaxb.Connections;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.Places;
import it.polito.dp2.RNS.sol3.jaxb.Rns;
import it.polito.dp2.RNS.sol3.jaxb.Vehicle;
import it.polito.dp2.RNS.sol3.jaxb.Vehicles;
import it.polito.dp2.RNS.sol3.service.db.RnsDB;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;



public class RnsService {
		private RnsDB db = RnsDB.getRnsDB();
		private InitRns initDB = InitRns.getInitRns();
		
		public RnsService() {
		}
		
		//get the root resource with links to some resources
		public Rns getRns(UriInfo uriInfo) {
	    	return fillRns(uriInfo);
		}
		
		//Get the list of all vehicles in the system
		public Vehicles getvehicles(UriInfo uriInfo) {
			Vehicles vehicles = new Vehicles();
			Collection<Vehicle> vehicleList = db.getVehicles();
			if (vehicleList != null){
				
				return vehicles;
			}else{
				return null;
			}
		}
		
		//Get the list of all places in the system
		public Places getPlaces(UriInfo uriInfo) {
			Places places = new Places();
			Collection<Place> listPlace = db.getPlaces();
			if (listPlace != null){
				for (Place p : listPlace){
					Place place = new Place();
					place = fillPlaceInfo(uriInfo,p);
					places.getPlace().add(place);
				}
				return places;
			}else{
				return null;
			}
		}
		
		//Get the list of all gates in the system
		public Places getGates(UriInfo uriInfo) {
			Places places = new Places();
			Collection<Place> gates = db.getGates();
			if (gates != null){
				for (Place p : gates){
					Place place = new Place();
					place = fillPlaceInfo(uriInfo,p);	
					places.getPlace().add(place);
				}
				return places;
			}else{
				return null;
			}
		}
		
		//Get the list of all road segments in the system
		public Places getRoadSegments(UriInfo uriInfo) {
			Places places = new Places();
			Collection<Place> roadSegments = db.getRoadSegments();
			if (roadSegments != null){
				for (Place p : roadSegments){
					Place place = new Place();
					place = fillPlaceInfo(uriInfo,p);	
					places.getPlace().add(place);
				}
				return places;
			}else{
				return null;
			}
		}
		
		//Get the list of all parking areas in the system
		public Places getParkingAreas(UriInfo uriInfo) {
			Places places = new Places();
			Collection<Place> parkingAreas = db.getParkingAreas();
			if (parkingAreas != null){
				for (Place p : parkingAreas){
					Place place = new Place();
					place = fillPlaceInfo(uriInfo,p);	
					places.getPlace().add(place);
				}
				return places;
			}else{
				return null;
			}
		}
		
		//Get the list of the connections on the db filled with all URIs
		public Connections getConnections(UriInfo uriInfo){
			Connections connections = new Connections();
	    	connections.setSelf(myUriBuilder(uriInfo.getAbsolutePathBuilder(),null));
	    	Collection<Connection> listConnection = db.getConnections();
	    	if (listConnection == null)
	    		return null;
			for (Connection c : listConnection){
				Connection connection = new Connection();
				connection.setFrom(c.getFrom());
				connection.setTo(c.getTo());
		    	connection.setFromLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places"+c.getFrom()));
		    	connection.setToLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+c.getTo()));
		    	
				connections.getConnection().add(connection);
			}
			if (connections == null)
				return null;
			return connections;
			
		}
		
		//Get a single place given the placeId, filled with all URIs
		public Place getPlace(UriInfo uriInfo,String placeId) {
			Place p = db.getPlace(placeId);
			if (p == null)
				return null;
			Place place = fillPlaceInfo(uriInfo,p);		
			return place;
		}
		
		
		private Place fillPlaceInfo(UriInfo uriInfo,Place p){
			Place place = new Place();
			place = p;
			place.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places"+p.getPlaceId()));
	    	place.setCurrentVehiclesLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places"+p.getPlaceId()+"/vehicles"));
	    	ConnectedTo connectedTo = new ConnectedTo();
	    	connectedTo.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places"+p.getPlaceId()+"/connectedTo"));
	    	for (To to: p.getConnectedTo().getTo()){
	    		To tmpTo = new To();
	    		tmpTo.setPlaceId(to.getPlaceId());
	    		tmpTo.setPlaceLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places"+tmpTo.getPlaceId()));
	    		connectedTo.getTo().add(tmpTo);
	    	}
	    	place.setConnectedTo(connectedTo);
			return place;
		}
		
		private String myUriBuilder(UriBuilder base,String pathRes){
			UriBuilder baseUri = base;
			if (pathRes != null)
				base.path(pathRes);
			return base.build().toString();
		}
		
		private Rns fillRns(UriInfo uriInfo){
	    	Rns rns = new Rns();
	    	rns.setSelf(myUriBuilder(uriInfo.getAbsolutePathBuilder(),null));
	    	rns.setPlaces(myUriBuilder(uriInfo.getAbsolutePathBuilder(),"/places"));
	    	rns.setVehicles(myUriBuilder(uriInfo.getAbsolutePathBuilder(),"/vehicles"));
	    	rns.setConnections(myUriBuilder(uriInfo.getAbsolutePathBuilder(),"/connections"));
	    	rns.setGates(myUriBuilder(uriInfo.getAbsolutePathBuilder(),"/places/gates"));
	    	rns.setParkingAreas(myUriBuilder(uriInfo.getAbsolutePathBuilder(),"/places/parkingAreas"));
	    	rns.setRoadSegments(myUriBuilder(uriInfo.getAbsolutePathBuilder(),"/places/roadSegments"));
	    	return rns;
		}

	

}

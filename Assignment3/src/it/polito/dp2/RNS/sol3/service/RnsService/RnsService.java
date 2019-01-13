package it.polito.dp2.RNS.sol3.service.RnsService;



import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.SAXException;

import it.polito.dp2.RNS.sol3.jaxb.ConnectedTo;
import it.polito.dp2.RNS.sol3.jaxb.ConnectedTo.To;
import it.polito.dp2.RNS.sol3.jaxb.Connection;
import it.polito.dp2.RNS.sol3.jaxb.Connections;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.Places;
import it.polito.dp2.RNS.sol3.jaxb.Position;
import it.polito.dp2.RNS.sol3.jaxb.Rns;
import it.polito.dp2.RNS.sol3.jaxb.State;
import it.polito.dp2.RNS.sol3.jaxb.SuggestedPath;
import it.polito.dp2.RNS.sol3.jaxb.SuggestedPath.Path;
import it.polito.dp2.RNS.sol3.jaxb.Vehicle;
import it.polito.dp2.RNS.sol3.jaxb.Vehicles;
import it.polito.dp2.RNS.sol3.service.db.RnsDB;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;
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
		private Neo4jServiceManager neo4jService = Neo4jServiceManager.getNeo4jServiceManager(); 
		
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
				for (Vehicle v : vehicleList){
					Vehicle vehicle = new Vehicle();
					vehicle = fillVehicleInfo(uriInfo,v);
					vehicles.getVehicle().add(vehicle);
				}
				return vehicles;
			}else{
				return null;
			}
		}
		
		//Get a single place given the placeId, filled with all URIs
		public Vehicle getVehicle(UriInfo uriInfo,String plateId) {
			Vehicle v = db.getVehicle(plateId);
			if (v == null)
				return null;
			Vehicle vehicle = fillVehicleInfo(uriInfo,v);		
			return vehicle;
		}
		
		//vehicle request to enter the system
		public SuggestedPath tryEnterVehicle(UriInfo uriInfo,Vehicle vehicle){
			String originId = vehicle.getOrigin();
			Place origin = db.getPlace(originId);
			if (origin != null){
				//check if origin is an IN / INOUT gate
				if ((origin.getParkingArea()==null)&&(origin.getRoadSegment()==null)){
					if (origin.getGate().equals("IN")||origin.getGate().equals("INOUT")){
						String destinationId = vehicle.getDestination();
						Place destination = db.getPlace(destinationId);
						if (destination != null){
							List<String> pathId = neo4jService.findShortestPath(originId,destinationId);
							if (pathId != null){
								SuggestedPath sp = new SuggestedPath();
								Vehicle addVehicle = new Vehicle();
								addVehicle = vehicle;
								Position position = new Position();
								position.setPlaceId(vehicle.getOrigin());
								addVehicle.setPosition(position);
								State state = new State();
								state.setVehicleState("IN_TRANSIT");
								addVehicle.setState(state);
								sp = fillSuggestedPathInfo(uriInfo,vehicle,pathId);
								for (Path p : sp.getPath()){
									System.out.println(p.getPalceId());
								}
								if ( db.addVehicle(addVehicle) == true){
									//vehicle added succesfully
									if (db.setSuggestedPath(addVehicle.getPlateId(), sp) == true)
										return sp;
								}
							}else{
								//error : null path
								
							}
						}else{
							//error : destination is not in the db
							throw new BadRequestException();
						}
					}else{
						//error is not a in or inout gate
						throw new BadRequestException();
					}
				}else{
					//error is not a gate
					throw new BadRequestException();
				}
			}else{
				//error : origin place is not in the system
				throw new BadRequestException();
			}
			return null;
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
		
		public SuggestedPath getSuggestedPath(UriInfo uriInfo,String plateId){
			SuggestedPath sp = db.getSuggestedPathByPlateId(plateId);
			if (sp == null)
				return null;
			return sp;
		}
		
		public State getVehicleState(UriInfo uriInfo,String plateId){
			Vehicle v = db.getVehicle(plateId);
			if (v == null)
				return null; // vehicle not found
			State state = new State();
			state.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+v.getPlateId()+"/state"));
			state.setVehicleState(v.getState().getVehicleState());
			return state;
		}
		
		public State changeVehicleState(UriInfo uriInfo,String plateId,State newState){
			Vehicle vehicle = db.getVehicle(plateId);
			if (vehicle == null)
				return null; // vehicle not found
			State oldState = vehicle.getState();
			if (newState.getVehicleState().equals("IN_TRANSIT")||newState.getVehicleState().equals("PARKED")){
				if (newState.equals(oldState)){
					newState.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+vehicle.getPlateId()+"/state"));
					return newState;
				}else{
					//update state
					if (db.changeState(plateId, newState)==true){	
						newState.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+vehicle.getPlateId()+"/state"));
						return newState;
					}else
						//internal server error
						throw new InternalServerErrorException();
				}
			}else{
				//bad request
				throw new BadRequestException();
			}
		}
		
		public Position getVehicleCurrentPosition(UriInfo uriInfo,String plateId){
			Vehicle v = db.getVehicle(plateId);
			if (v == null)
				return null; // vehicle not found
			Position position = new Position();
			position.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+v.getPlateId()+"/currentPosition"));
			position.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+v.getPosition().getPlaceId()));
			position.setPlaceId(v.getPosition().getPlaceId());
			return position;
		}
		
		public SuggestedPath changeCurrentPosition(UriInfo uriInfo,String plateId,Position newPosition){
			Vehicle vehicle = db.getVehicle(plateId);
			if (vehicle != null){
				Place newPlace = db.getPlace(newPosition.getPlaceId());
				if ( newPlace != null){
					if (newPlace.getPlaceId().equals(vehicle.getPosition().getPlaceId())){
						//new position is equal to current position -> nothing change
						SuggestedPath sp = db.getSuggestedPathByPlateId(plateId);
						return sp;
					}else{
						// try to change position
						SuggestedPath sp = db.getSuggestedPathByPlateId(plateId);
						Position oldPosition = vehicle.getPosition();
						if (checkIfInSuggestedPath(sp,oldPosition.getPlaceId(),newPosition.getPlaceId()) == true){
							//new pos is the next pos in the suggested path
							if (db.changePositionWithoutSuggestedPath(plateId, newPosition)==true)
								return sp;
							else
								return null;//internal server error
						}else{
							//new pos is not the next in suggested path
							//check if the current position is connectedTo the new position
							if (checkIfIsNextTo(oldPosition.getPlaceId(),newPosition.getPlaceId())==true){
								//the old place is connected to the new place
								//look if exist a suggested path from the new position
								//from= new position -> destination= vehicle destination
								List<String> pathId = neo4jService.findShortestPath(newPosition.getPlaceId(),vehicle.getDestination());
								if (pathId != null){
									SuggestedPath newSp = new SuggestedPath();
									newSp = fillSuggestedPathInfo(uriInfo,vehicle,pathId);
									if (db.changePositionWithSuggestedPath(newSp,plateId,newPosition)==true){
										return newSp;
									}else{
										//internal server error : impossible update sp
										throw new InternalServerErrorException();
									}
								}else{
									//error : null path
									
								}
							}else{
								//error : the old place is not connected to the new place
								throw new BadRequestException();
							}
						}
						
					}
				}else{
					//new position not found in the db
					throw new BadRequestException();
				}
			}else{
				//vehicle not found
				throw new NotFoundException();
			}
			return null;
		}
		
		//this method return true if the next place is the next on the suggested path,false otherwise
		private boolean checkIfInSuggestedPath(SuggestedPath sp,String oldP,String newP){
			String previous = null;
			for (Path p : sp.getPath()){
				if (previous==null)
					previous=p.getPalceId();
				if ((oldP.equals(previous))&&(newP.equals(p.getPalceId()))){
					//next place is the next in the suggested path
					return true;
				}else{
					previous=p.getPalceId();
				}
			}
			return false;			
		}
		
		private boolean checkIfIsNextTo(String oldP,String newP){
			Place oldPlace = db.getPlace(oldP);
			if (oldPlace.getConnectedTo().getTo().contains(newP) == true)
				return true;
			return false;			
		}
		
		public Vehicles getCurrentVehiclesByPlaceId(UriInfo uriInfo,String placeId){
			Vehicles vehicles = new Vehicles();
			Collection<Vehicle> vehicleList = db.getCurrentVehiclesByPlaceId(placeId);
			if (vehicleList != null){
				for (Vehicle v : vehicleList){
					Vehicle vehicle = new Vehicle();
					vehicle = fillVehicleInfo(uriInfo,v);
					vehicles.getVehicle().add(vehicle);					
				}
			}
			vehicles.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+placeId+"/currentVehicles"));
			return vehicles;
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
		    	connection.setFromLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+c.getFrom()));
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
			place.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+p.getPlaceId()));
	    	place.setCurrentVehiclesLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+p.getPlaceId()+"/currentVehicles"));
	    	ConnectedTo connectedTo = new ConnectedTo();
	    	connectedTo.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+p.getPlaceId()+"/connectedTo"));
	    	for (To to: p.getConnectedTo().getTo()){
	    		To tmpTo = new To();
	    		tmpTo.setPlaceId(to.getPlaceId());
	    		tmpTo.setPlaceLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+tmpTo.getPlaceId()));
	    		connectedTo.getTo().add(tmpTo);
	    	}
	    	place.setConnectedTo(connectedTo);
			return place;
		}
		
		private Vehicle fillVehicleInfo(UriInfo uriInfo,Vehicle v){
			Vehicle vehicle = new Vehicle();
			vehicle = v;
			vehicle.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+v.getPlateId()));
			vehicle.setDestinationLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+v.getOrigin()));
			vehicle.setOriginLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+v.getDestination()));
			vehicle.setState(v.getState());
			vehicle.getState().setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+v.getPlateId()+"/state"));
			Position position = new Position();
			position.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+v.getPlateId()+"/currentPosition"));
			position.setPlaceId(v.getOrigin());
			position.setPlaceLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+v.getOrigin()));
			vehicle.setSuggestedPathLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+v.getPlateId()+"/suggestedPath"));
			return vehicle;
		}
		
		private SuggestedPath fillSuggestedPathInfo(UriInfo uriInfo,Vehicle v,List<String> path){
			SuggestedPath sp = new SuggestedPath();
			
			sp.setStartId(v.getOrigin());
			sp.setEndId(v.getDestination());
			sp.setVehicle(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+v.getPlateId()));
			sp.setSelf(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/vehicles/"+v.getPlateId()+"/suggestedPath"));
			sp.setStart(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+v.getOrigin()));
			sp.setEnd(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+v.getDestination()));
			for (String place : path){
				Path p = new Path();
				p.setPalceId(place);
				p.setPlaceLink(myUriBuilder(uriInfo.getBaseUriBuilder(),"/rns/places/"+place));
				sp.getPath().add(p);
			}
			return sp;
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

package it.polito.dp2.RNS.sol3.vehClient;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.lab3.EntranceRefusedException;
import it.polito.dp2.RNS.lab3.ServiceException;
import it.polito.dp2.RNS.lab3.UnknownPlaceException;
import it.polito.dp2.RNS.lab3.WrongPlaceException;
import it.polito.dp2.RNS.sol3.jaxb.rnsSystem.Position;
import it.polito.dp2.RNS.sol3.jaxb.rnsSystem.Rns;
import it.polito.dp2.RNS.sol3.jaxb.rnsSystem.State;
import it.polito.dp2.RNS.sol3.jaxb.rnsSystem.SuggestedPath;
import it.polito.dp2.RNS.sol3.jaxb.rnsSystem.SuggestedPath.Path;
import it.polito.dp2.RNS.sol3.jaxb.rnsSystem.Vehicle;

public class VehServiceManager {
	//private static VehServiceManager vehicleService = new VehServiceManager();
	private static String serviceBaseUri = System.getProperty("it.polito.dp2.RNS.lab3.URL");
	private Client client;
	private Rns rnsInfo;
	private SuggestedPath currentSuggestedPath;
	private Vehicle vehicle;
	private String currentPosition = null;
	
	public VehServiceManager(){
		if (serviceBaseUri == null) {
            String servicePort = System.getProperty("PORT");
            if (servicePort == null) {
                servicePort = "8080";
            }
            serviceBaseUri = "http://localhost:" + servicePort + "/RnsSystem/rest";
        }
		client = ClientBuilder.newClient();
		loadInfoResources();
	}
	
	/*public static VehServiceManager getVehServiceManager(){
		return vehicleService;
	}*/
	
	public void loadInfoResources(){
		//get base info of the web service (all link to principals resources)
		Response serverResponse = client.target(UriBuilder.fromUri(serviceBaseUri).path("/rns").build())
                .request()
                .accept(MediaType.APPLICATION_XML)
                .get();
		
		if (serverResponse.getStatusInfo().getStatusCode() != 200){
			// error
		}
		
		rnsInfo = serverResponse.readEntity(Rns.class);
	}
	
	public List<String> enterRequest(String plateId, VehicleType type, String inGate, String destination) throws ServiceException, WrongPlaceException, UnknownPlaceException, EntranceRefusedException{
		//create the request
		Vehicle v = new Vehicle();
		v.setPlateId(plateId);
		v.setVehicleType(type.value());
		v.setOrigin(inGate);
		v.setDestination(destination);
		System.out.println("enter request to " + rnsInfo.getVehicles() + "/"+plateId);
		System.out.println(v.getPlateId());
		System.out.println(v.getOrigin());
		System.out.println(v.getDestination());
		System.out.println(v.getVehicleType());
		Response serverResponse = client.target(UriBuilder.fromUri(rnsInfo.getVehicles()).path("/"+plateId).build())
                .request()
                .accept(MediaType.APPLICATION_XML)
                .put(Entity.xml(v));
		
		if (serverResponse.getStatusInfo().getStatusCode() != 200){
			// error
			if (serverResponse.getStatusInfo().getStatusCode() >= 500)
				throw new ServiceException();
			if (serverResponse.getStatusInfo().getStatusCode() == 403)
				throw new WrongPlaceException();
			if (serverResponse.getStatusInfo().getStatusCode() == 400)
				throw new UnknownPlaceException();
			
			throw new EntranceRefusedException();
		}
		System.out.println("succesfully entered");
		SuggestedPath suggestedPath = serverResponse.readEntity(SuggestedPath.class);
		List<String> path = new ArrayList<String>();
		if (suggestedPath.getPath().isEmpty()==false){
			for (Path p : suggestedPath.getPath())
				path.add(p.getPalceId());
		}
		currentSuggestedPath = suggestedPath;
		
		getVehicleFromService(plateId);
		currentPosition = inGate;
		return path;
	}
	
	private void getVehicleFromService(String plateId){

		Response serverResponse = client.target(UriBuilder.fromUri(rnsInfo.getVehicles()).path("/"+plateId).build())
                .request()
                .accept(MediaType.APPLICATION_XML)
                .get();
		
		if (serverResponse.getStatusInfo().getStatusCode() != 200){
			// error
		}
		
		vehicle = serverResponse.readEntity(Vehicle.class);
		return;
	}
	
	public List<String> moveRequest(String newP) throws ServiceException, WrongPlaceException, UnknownPlaceException{
		//create the request
		Position newPosition = new Position();
		newPosition.setPlaceId(newP);
		System.out.println("move request to "+newP + " by "+vehicle.getPlateId());
		Response serverResponse = client.target(UriBuilder.fromUri(vehicle.getCurrentPositionLink()).build())
                .request()
                .accept(MediaType.APPLICATION_XML)
                .put(Entity.xml(newPosition));
		

		if (serverResponse.getStatusInfo().getStatusCode() != 200){
			// error
			if (serverResponse.getStatusInfo().getStatusCode() >= 500)
				throw new ServiceException();
			if (serverResponse.getStatusInfo().getStatusCode() == 403){
				List<String> path = new ArrayList<String>();
				for (Path p : currentSuggestedPath.getPath()){
					path.add(p.getPalceId());
					System.out.println(p.getPalceId());
					
				}
				System.out.println("sugg path is not changed and the return null");
				return null;
			}
			if (serverResponse.getStatusInfo().getStatusCode() == 400)
				throw new UnknownPlaceException();
		}
		SuggestedPath newSuggestedPath = serverResponse.readEntity(SuggestedPath.class);
		
		System.out.println("returned path");
		System.out.println(newSuggestedPath.getStartId());
		System.out.println(newSuggestedPath.getEndId());
	
		System.out.println("path:");
		List<String> path = new ArrayList<String>();
		if (newSuggestedPath.getPath().isEmpty()==false){
			for (Path p : newSuggestedPath.getPath()){
				path.add(p.getPalceId());
				System.out.println(p.getPalceId());
			}
			currentPosition = newP;
			if (newSuggestedPath.getStartId().equals(currentSuggestedPath.getStartId())&&newSuggestedPath.getEndId().equals(currentSuggestedPath.getEndId()))
				return null;
				
			currentSuggestedPath = newSuggestedPath;
			return path;
		}
		System.out.println("---------");
		currentPosition = newP;
		return path;
	}
	
	public void changeStateRequest(VehicleState newState){
		
		State newStateData = new State();
		newStateData.setVehicleState(newState.value());
		
		Response serverResponse = client.target(UriBuilder.fromUri(vehicle.getChangeStateLink()).build())
                .request()
                .accept(MediaType.APPLICATION_XML)
                .put(Entity.xml(newStateData));
		
		if (serverResponse.getStatusInfo().getStatusCode() != 200){
			// error
		}
		return;
	}
	
	public void exitRequest(String outGate) throws ServiceException, WrongPlaceException, UnknownPlaceException{
		System.out.println("exit request with current position " + currentPosition + " by "+vehicle.getPlateId());
		if (currentPosition.equals(outGate)==false){
			moveRequest(outGate);			
		}

		Response serverResponse = client.target(UriBuilder.fromUri(vehicle.getExitRequestLink()).build())
                .request()
                .accept(MediaType.APPLICATION_XML)
                .delete();
		

		if (serverResponse.getStatusInfo().getStatusCode() != 200){
			// error
			if (serverResponse.getStatusInfo().getStatusCode() >= 500)
				throw new ServiceException();
			if (serverResponse.getStatusInfo().getStatusCode() == 403)
				throw new WrongPlaceException();
			
		}
		
		return;
	}
}

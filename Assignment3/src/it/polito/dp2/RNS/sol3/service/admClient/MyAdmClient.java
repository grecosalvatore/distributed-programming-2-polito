package it.polito.dp2.RNS.sol3.admClient;

import java.util.Calendar;
import java.util.Set;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.GateType;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.VehicleReader;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.lab3.AdmClient;
import it.polito.dp2.RNS.lab3.ServiceException;

public class MyAdmClient implements AdmClient{

	@Override
	public Set<ConnectionReader> getConnections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<GateReader> getGates(GateType arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ParkingAreaReader> getParkingAreas(Set<String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlaceReader getPlace(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PlaceReader> getPlaces(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<RoadSegmentReader> getRoadSegments(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VehicleReader getVehicle(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<VehicleReader> getVehicles(Calendar arg0, Set<VehicleType> arg1, VehicleState arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<VehicleReader> getUpdatedVehicles(String place) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VehicleReader getUpdatedVehicle(String id) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}

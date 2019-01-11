package it.polito.dp2.RNS.sol3.vehClient;

import java.util.List;

import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.lab3.EntranceRefusedException;
import it.polito.dp2.RNS.lab3.ServiceException;
import it.polito.dp2.RNS.lab3.UnknownPlaceException;
import it.polito.dp2.RNS.lab3.VehClient;
import it.polito.dp2.RNS.lab3.WrongPlaceException;

public class MyVehClient implements VehClient{

	@Override
	public List<String> enter(String plateId, VehicleType type, String inGate, String destination)
			throws ServiceException, UnknownPlaceException, WrongPlaceException, EntranceRefusedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> move(String newPlace) throws ServiceException, UnknownPlaceException, WrongPlaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeState(VehicleState newState) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit(String outGate) throws ServiceException, UnknownPlaceException, WrongPlaceException {
		// TODO Auto-generated method stub
		
	}

}

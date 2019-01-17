package it.polito.dp2.RNS.sol3.admClient;

import it.polito.dp2.RNS.sol3.admClient.library.MyRnsType;
import it.polito.dp2.RNS.sol3.jaxb.admClient.PlaceType;
import it.polito.dp2.RNS.sol3.jaxb.admClient.RnsType;
import it.polito.dp2.RNS.sol3.jaxb.rnsSystem.Rns;

public class AdmDB {
	private Rns rnsInfoUri;
	private static AdmDB admjDB = new AdmDB();
	private MyRnsType rns;
	private AdmDB(){
		rns = new MyRnsType();
	}
	
	public static AdmDB getAdmDB() {
		return admjDB;
	}
	
	public void setRnsInfoUri(Rns info){
		rnsInfoUri = info;
		return;
	}
	
	public Rns getRnsInfoUri(){
		return rnsInfoUri;
	}
	
	public MyRnsType getRns(){
		return rns;
	}
}

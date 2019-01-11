package it.polito.dp2.RNS.sol3.service.db;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.neo4j.Node;

public class Neo4jDB {
	private static Neo4jDB neo4jDB = new Neo4jDB();
	private HashMap<String,Node> nodeByPlaceId; 
	
	private Neo4jDB() {
		nodeByPlaceId = new HashMap<String,Node>();
	}
	
	public static Neo4jDB getNeo4jDB() {
		return neo4jDB;
	}
	
	public Node getNodeByPlaceId(String placeId){
		if (nodeByPlaceId.containsKey(placeId))
			return nodeByPlaceId.get(placeId);
		return null;
	}
	
	public boolean addNode(String placeId,Node node){
		if (nodeByPlaceId.containsKey(placeId))
			return false;
		else{
			nodeByPlaceId.put(placeId, node);
			return true;
		}
	}
}

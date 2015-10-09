package fr.peertopeer.objects.request;

import java.io.Serializable;
import java.util.UUID;

import fr.peertopeer.objects.Pair;
import fr.peertopeer.server.Server;

public class ConnectionRequest extends Request implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Pair newPair;
	
	public ConnectionRequest() {
	}
	
	public ConnectionRequest(Pair newPair) {
		this.newPair = newPair;
	}

	public Pair getNewPair() {
		return newPair;
	}

	public void setNewPair(Pair newPair) {
		this.newPair = newPair;
	}

	@Override
	public Object build(Server server) {
		newPair.setUuid(UUID.randomUUID());
		server.addPair(newPair);
		System.out.println("Add pair "+newPair.getUuid().toString());
		return newPair;
	}
}

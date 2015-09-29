package fr.peertopeer.objects.request;

import java.io.Serializable;

import fr.peertopeer.server.Server;

public abstract class Request implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private boolean broadcast = false;

	public abstract Object build(Server server);

	public boolean isBroadcast() {
		return broadcast;
	}

	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}

	
}

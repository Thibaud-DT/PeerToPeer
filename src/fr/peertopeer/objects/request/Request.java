package fr.peertopeer.objects.request;

import java.io.Serializable;

import fr.peertopeer.server.Server;

public abstract class Request implements Serializable{


	private static final long serialVersionUID = 1L;

	public abstract Object build(Server server);
	
}

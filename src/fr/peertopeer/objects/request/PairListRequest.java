package fr.peertopeer.objects.request;

import java.io.Serializable;

import fr.peertopeer.server.Server;

public class PairListRequest extends Request implements Serializable{

	private static final long serialVersionUID = 1L;

	@Override
	public Object build(Server server) {
		return server.getPairsList();
	}

}

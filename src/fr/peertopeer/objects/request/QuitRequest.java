package fr.peertopeer.objects.request;

import fr.peertopeer.objects.Pair;
import fr.peertopeer.server.Server;

public class QuitRequest extends Request{

	private static final long serialVersionUID = 1L;
	
	private Pair pairQuiting;
	
	public QuitRequest(Pair pairQuiting){
		this.setPairQuiting(pairQuiting);
	}

	@Override
	public Object build(Server server) {
		if(response == null){
			server.removePair(getPairQuiting());
			response = server.getPairsList();
		}
		return response;
	}

	public Pair getPairQuiting() {
		return pairQuiting;
	}

	public void setPairQuiting(Pair pairQuiting) {
		this.pairQuiting = pairQuiting;
	}

}

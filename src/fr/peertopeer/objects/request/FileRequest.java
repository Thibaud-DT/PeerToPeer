package fr.peertopeer.objects.request;

import java.io.IOException;

import fr.peertopeer.client.Client;
import fr.peertopeer.client.ThreadSendFile;
import fr.peertopeer.objects.FileShared;
import fr.peertopeer.objects.Pair;
import fr.peertopeer.server.Server;
import fr.peertopeer.utils.Logger;

public class FileRequest extends Request{
	
	private static final long serialVersionUID = 1L;
	
	private String filename;
	private Pair requester;

	public FileRequest(Pair requester,String filename) {
		this.setRequester(requester);
		this.filename = filename;
	}
	
	
	@Override
	public Object build(Server server) {
		return null;
	}
	
	public Object build(Client client) {
		ThreadSendFile tSendFile ;
		try {
			tSendFile = new ThreadSendFile(client, filename);
			tSendFile.start();
			return new FileShared(client.getMe(), tSendFile.getSharedPort(), filename);
		} catch (IOException e) {
			Logger.getInstance().error(e.getMessage());
			return null;
		}	
	}

	public Pair getRequester() {
		return requester;
	}

	public void setRequester(Pair requester) {
		this.requester = requester;
	}

}

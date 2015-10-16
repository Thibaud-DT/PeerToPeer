package fr.peertopeer.main;

import java.net.InetAddress;
import java.net.UnknownHostException;

import fr.peertopeer.client.Client;
import fr.peertopeer.utils.Logger;

public class ClientProgram {
	static private Logger logger = Logger.getInstance();
	
	public static void main(String[] args) {
		if(args.length == 3){
			Client client = null;
			try {
				client = new Client(InetAddress.getByName(args[0]), Integer.valueOf(args[1]), args[2]);
			
				logger.success("Client connecté.");
			
			} catch (NumberFormatException | UnknownHostException e) {
				logger.error(e.getMessage());
			}
		}else{
			logger.info("Bad Option !");
			logger.info("java ClientProgram [server adresse] [port] [path shared]");
		}
		
	}

}

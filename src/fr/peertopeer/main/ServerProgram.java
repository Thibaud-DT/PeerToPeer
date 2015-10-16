package fr.peertopeer.main;

import fr.peertopeer.server.Server;
import fr.peertopeer.utils.Logger;

public class ServerProgram {
	static private Logger logger = Logger.getInstance();
	
	public static void main(String[] args) {
		if(args.length == 1){
			Server server = new Server(Integer.valueOf(args[0]));
			server.go();
		}else{
			logger.info("Bad Option !");
			logger.info("java ServerProgram [port]");
		}
		
		
	}
	
}

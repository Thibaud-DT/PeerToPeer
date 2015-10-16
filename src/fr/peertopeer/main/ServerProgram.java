package fr.peertopeer.main;

import java.io.File;
import java.util.Scanner;
import java.util.UUID;
import java.util.Map.Entry;

import fr.peertopeer.objects.Pair;
import fr.peertopeer.server.Server;
import fr.peertopeer.utils.Logger;

public class ServerProgram {
	static private Logger logger = Logger.getInstance();
	
	public static void main(String[] args) {
		if(args.length >= 1){
			if(args.length == 2 && args[1].equals("-d"))
				logger.setDebug(true);
			Server server = new Server(Integer.valueOf(args[0]));
			server.go();
			
			logger.info("Voici la liste des commandes utilisable :");
			logger.info("[HELP] QUIT - Ferme le serveur.");
			logger.info("[HELP] HELP - Renvoie la page d'aide.");
			
			Scanner sc = new Scanner(System.in);
			while (true){
				String[] in = sc.nextLine().split(" ");
				
				switch (in[0]) {
				case "QUIT":
					server.stop();
					System.exit(0);
					break;

				default:
					logger.info("Voici la liste des commandes utilisable :");
					logger.info("[HELP] QUIT - Ferme le serveur.");
					logger.info("[HELP] HELP - Renvoie la page d'aide.");
					break;
				}
			}
		}else{
			logger.info("Bad Option !");
			logger.info("java ServerProgram [port] ([-d] for debug - optionnal)");
		}
		
		
	}
	
}

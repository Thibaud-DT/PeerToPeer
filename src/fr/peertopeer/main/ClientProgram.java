package fr.peertopeer.main;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;

import fr.peertopeer.client.Client;
import fr.peertopeer.objects.Pair;
import fr.peertopeer.utils.Logger;

public class ClientProgram {
	static private Logger logger = Logger.getInstance();

	public static void main(String[] args) {
		if (args.length >= 3) {
			if(args.length == 4 && args[3].equals("-d"))
				logger.setDebug(true);
				
			Client client = null;
			try {
				client = new Client(InetAddress.getByName(args[0]), Integer.valueOf(args[1]), args[2]);
				logger.success("Client connecté.");
				
				logger.info("Voici la liste des commandes utilisable :");
				logger.info("[HELP] LISTPAIR - Récupère la liste des pairs connectés.");
				logger.info("[HELP] LISTFILE [UUID] - Récupère la liste des fichiers du pair.");
				logger.info("[HELP] GET [UUID] [FileName] - Envoie une requête de récupération du fichier au pair précisé.");
				logger.info("[HELP] QUIT - Ferme le client.");
				logger.info("[HELP] HELP - Renvoie la page d'aide.");
				
				Scanner sc = new Scanner(System.in);
				while (true){
					String[] in = sc.nextLine().split(" ");
					
					switch (in[0]) {
					case "GET":
						if(in.length == 3){
							client.sendFileRequest(UUID.fromString(in[1]), in[2]);						
						}
						else{
							logger.info("Pour demander un fichier :");
							logger.info("GET [UUID] [Nom du fichier]");
						}
						break;
					case "LISTPAIR":
						if(in.length == 1){
							for(Entry<UUID, Pair>pair : client.getPairsList().entrySet()){
								if(pair.getKey() != client.getMe().getUuid())
									logger.info("UUID : "+pair.getKey());
							}
						}
						else{
							logger.info("Pour demander la liste des pair :");
							logger.info("LISTPAIR");
						}
						break;
					case "LISTFILE":
						if(in.length == 2){
							Pair pair = client.getPairsList().get(UUID.fromString(in[1]));
							logger.info("Liste des fichiers de "+pair.getUuid()+" :");
							for(File file : pair.getSharedFiles()){
								logger.info("\t"+file.getName());
							}				
						}
						else{
							logger.info("Pour demander la liste des fichier d'un pair :");
							logger.info("LISTFILE [UUID]");
						}
						break;
					case "QUIT":
						client.close();
						System.exit(0);
						break;

					default:
						logger.info("Voici la liste des commandes utilisable :");
						logger.info("[HELP] LISTPAIR - Récupère la liste des pairs connectés.");
						logger.info("[HELP] LISTFILE [UUID] - Récupère la liste des fichiers du pair.");
						logger.info("[HELP] GET [UUID] [FileName] - Envoie une requête de récupération du fichier au pair précisé.");
						logger.info("[HELP] QUIT - Ferme le client.");
						logger.info("[HELP] HELP - Renvoie la page d'aide.");
						break;
					}
				}

			} catch (NumberFormatException | UnknownHostException e) {
				logger.error(e.getMessage());
			}
			
		} else {
			logger.info("Bad Option !");
			logger.info("java ClientProgram [server adresse] [port] [path shared] ([-d] for debug - optionnal)");
		}

	}

}

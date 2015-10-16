package fr.peertopeer.client;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;

import fr.peertopeer.objects.FileShared;
import fr.peertopeer.utils.Logger;

public class ThreadReceiveFile extends Thread{

	private Client client;
	private FileShared fileShared;
	
	private Logger logger = Logger.getInstance();

	public ThreadReceiveFile(Client client,FileShared fileShared) {
		this.client = client;
		this.fileShared = fileShared;
	}
	
	@Override
	public void run() {
		Socket sock = null;
		try {
			Thread.sleep(5000);
			logger.debug("Tentative de connexion a "+fileShared.getOther().getAdress()+":"+fileShared.getPortShared());
			sock = new Socket(fileShared.getOther().getAdress(), fileShared.getPortShared());
			logger.info("Connected to Pair");
			DataInputStream dis = null;
			dis = new DataInputStream(sock.getInputStream());
			logger.info("Start receive file");
			receiveFile(dis);
		} catch (IOException | InterruptedException e) {
			logger.error(e.getMessage());
		}
		
		logger.debug("ThreadReceiver for "+fileShared.getFileName()+" is closed.");
		
		this.interrupt();
	}

	private void receiveFile(DataInputStream in) {
		FileWriter ofile = null;
		try{
			ofile = new FileWriter(client.getPathSharedFiles()+"/"+fileShared.getFileName());
		
			while(true){
				byte b = in.readByte();
				ofile.write(b);
			}
		}catch(EOFException end){
			logger.info("Téléchargement de "+fileShared.getFileName()+" terminé.");
		}catch(IOException e){
			logger.error(e.getMessage());
		}		
	}
}

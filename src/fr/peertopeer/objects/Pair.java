package fr.peertopeer.objects;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Pair implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private UUID uuid;
	private InetAddress adress;
	private int clientPort;
	private List<File> sharedFiles;

	private int port;
	
	public Pair() {
		sharedFiles = new ArrayList<File>();
	}
	
	public Pair(InetAddress adress, int port, int clientPort, List<File> sharedFiles) {
		this.adress = adress;
		this.setPort(port);
		this.clientPort = clientPort;
		this.sharedFiles = sharedFiles;
		this.uuid = UUID.randomUUID();
	}
	
	public Pair(UUID uuid, InetAddress adress, int clientPort, List<File> sharedFiles) {
		this.uuid = uuid;
		this.adress = adress;
		this.clientPort = clientPort;
		this.sharedFiles = sharedFiles;
	}
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public InetAddress getAdress() {
		return adress;
	}
	public void setAdress(InetAddress adress) {
		this.adress = adress;
	}
	public int getClientPort() {
		return clientPort;
	}
	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}
	public List<File> getSharedFiles() {
		return sharedFiles;
	}
	public void setSharedFiles(List<File> sharedFiles) {
		this.sharedFiles = sharedFiles;
	}
	public void addFile(File file) {
		sharedFiles.add(file);
	}

	@Override
	public String toString() {
		return "Pair [uuid=" + uuid + ", adress=" + adress + ", port=" + port + ", filePort=" + clientPort + ", sharedFiles=" + sharedFiles
				+ "]";
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}

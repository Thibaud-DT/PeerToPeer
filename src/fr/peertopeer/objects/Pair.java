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
	private int filePort;
	private List<File> sharedFiles;

	private int port;
	
	public Pair() {
		sharedFiles = new ArrayList<File>();
	}
	
	public Pair(InetAddress adress,int port, int filePort, List<File> sharedFiles) {
		this.adress = adress;
		this.setPort(port);
		this.filePort = filePort;
		this.sharedFiles = sharedFiles;
		this.uuid = UUID.randomUUID();
	}
	
	public Pair(UUID uuid, InetAddress adress, int filePort, List<File> sharedFiles) {
		this.uuid = uuid;
		this.adress = adress;
		this.filePort = filePort;
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
	public int getFilePort() {
		return filePort;
	}
	public void setFilePort(int filePort) {
		this.filePort = filePort;
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
		return "Pair [uuid=" + uuid + ", adress=" + adress + ", port=" + port + ", filePort=" + filePort + ", sharedFiles=" + sharedFiles
				+ "]";
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}

package fr.peertopeer.client;

import java.net.InetAddress;
import java.util.UUID;

public class ClientInfo {

	private UUID uuid;
	private InetAddress address;
	private int port;
	
	public ClientInfo() {
		this(null,0,null);
	}
	
	public ClientInfo(InetAddress address, int port){
		this(address,port,null);
	}
	
	public ClientInfo(InetAddress address, int port, UUID uuid){
		this.address = address;
		this.port = port;
		this.uuid = uuid;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public String toString() {
		return getUuid()+" ["+getAddress()+":"+getPort();
	}
}

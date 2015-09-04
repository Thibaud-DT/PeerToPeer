package fr.peertopeer.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class Client {

	private final static int _dgLength = 50;
	
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	
	private ClientInfo clientInfo;
	
	private List<ClientInfo> clients;
	
	public Client() {}
	
	
	
	
	
	public boolean connect(InetAddress host, int port){
		// TODO
		return true;
	}
	
	public List<ClientInfo> retrieve(){
		// TODO
		return null;
	}
	
	public boolean disconnect(){
		// TODO
		return true;
	}
	
	
	
	public ClientInfo getClientInfo(){
		return this.clientInfo;
	}
	
	
	
	private String receive() throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		return new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
	}
	
	private String receive(int _dgLength) throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		return new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
	}
	
	private void send(String msg, InetAddress address, int port) throws IOException {
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(address);
		dgPacket.setPort(port);
		dgSocket.send(dgPacket);
	}
	
	
}

package fr.peertopeer.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import fr.peertopeer.objects.Pair;
import fr.peertopeer.objects.PairList;
import fr.peertopeer.objects.request.NewPairRequest;
import fr.peertopeer.objects.request.PairListRequest;
import fr.peertopeer.utils.Serializer;

public class Client {
	private List<Pair> pairsList;
	private Pair me;
	private DatagramSocket socket;
	private InetAddress serverAdress;
	private int serverPort;
	private byte[] bufferReceived;
	
	public Client(InetAddress serverAdress, int serverPort, int tcpSharedFilesPort) {
		try {
			socket = new DatagramSocket();
			bufferReceived = new byte[socket.getReceiveBufferSize()];
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serverPort = serverPort;
		this.serverAdress = serverAdress;
		me = new Pair(socket.getLocalAddress(), tcpSharedFilesPort, null);
	}
	
	public void pair() {
		NewPairRequest newMe = new NewPairRequest(me);
		byte[] datas = Serializer.serialize(newMe);
		System.out.println("New pair on "+serverAdress.getHostName());
		DatagramPacket packet = new DatagramPacket(datas, datas.length, serverAdress, serverPort);
		try {
			socket.send(packet);
			DatagramPacket rPacket = new DatagramPacket(bufferReceived, bufferReceived.length);
			socket.receive(rPacket);
			me = (Pair)Serializer.deserialize(rPacket.getData());
			System.out.println(me);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void retrievePairsList() {
		byte[] datas = Serializer.serialize(new PairListRequest());
		DatagramPacket packet = new DatagramPacket(datas, datas.length, serverAdress, serverPort);
		try {
			System.out.println("Retrieving pairs...");
			socket.send(packet);
			DatagramPacket rPacket = new DatagramPacket(bufferReceived, bufferReceived.length);
			socket.receive(rPacket);
			pairsList = (List<Pair>)Serializer.deserialize(rPacket.getData());
			System.out.println(pairsList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = null;
		try {
			client = new Client(InetAddress.getByName(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]));
		} catch (NumberFormatException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.pair();
		client.retrievePairsList();
	}
}

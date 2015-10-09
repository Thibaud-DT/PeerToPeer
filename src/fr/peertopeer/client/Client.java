package fr.peertopeer.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.peertopeer.objects.Pair;
import fr.peertopeer.objects.request.ConnectionRequest;
import fr.peertopeer.objects.request.PairListRequest;
import fr.peertopeer.objects.request.QuitRequest;
import fr.peertopeer.objects.request.Request;
import fr.peertopeer.utils.Serializer;

public class Client {	
	
	private Map<UUID,Pair> pairsList;
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
		me = new Pair(socket.getLocalAddress(), socket.getLocalPort(),tcpSharedFilesPort, null);
	}
	
	public void send(Request request) throws IOException{
		// On Serialize la Request
		byte[] datas = Serializer.serialize(request);
		
		// On envoie la Request serialized
		DatagramPacket packet = new DatagramPacket(datas, datas.length, serverAdress, serverPort);
		socket.send(packet);
		
		// On ecoute la reponse
		DatagramPacket rPacket = new DatagramPacket(bufferReceived, bufferReceived.length);
		socket.receive(rPacket);
		
		// On traite la reponse
		receiveResponse(Serializer.deserialize(rPacket.getData()));
		
	}
	
	@SuppressWarnings("unchecked")
	private void receiveResponse(Object response){
		if(response instanceof Pair){
			me = (Pair)response;
		}else if(response instanceof List<?>){
			pairsList = (Map<UUID,Pair>) response;
		}
	}
	
	public static void main(String[] args) {
		Client client = null;
		try {
			client = new Client(InetAddress.getByName(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]));
		} catch (NumberFormatException | UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			client.send(new ConnectionRequest(client.me));
			client.send(new PairListRequest());
			System.out.println(client.pairsList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void close() {
		socket.close();
	}
}

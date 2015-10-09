package fr.peertopeer.client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

	private Map<UUID, Pair> pairsList;
	private Pair me;
	private DatagramSocket socket;
	private InetAddress serverAdress;
	private int serverPort;
	private byte[] bufferReceived;
	private FilesSharedSocket filesSSocket = null;
	

	public Client(InetAddress serverAdress, int serverPort,
			int tcpSharedFilesPort, String pathSharedFiles) {
		try {
			socket = new DatagramSocket();
			bufferReceived = new byte[socket.getReceiveBufferSize()];
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serverPort = serverPort;
		this.serverAdress = serverAdress;
		try {
			filesSSocket = new FilesSharedSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		me = new Pair(socket.getLocalAddress(), socket.getLocalPort(),
				filesSSocket.getLocalPort(), getFilesToShared(pathSharedFiles));
	}

	public void send(Request request) throws IOException {
		// On Serialize la Request
		byte[] datas = Serializer.serialize(request);

		// On envoie la Request serialized
		DatagramPacket packet = new DatagramPacket(datas, datas.length,
				serverAdress, serverPort);
		socket.send(packet);

		// On ecoute la reponse
		DatagramPacket rPacket = new DatagramPacket(bufferReceived,
				bufferReceived.length);
		socket.receive(rPacket);

		// On traite la reponse
		receiveResponse(Serializer.deserialize(rPacket.getData()));

	}

	@SuppressWarnings("unchecked")
	private void receiveResponse(Object response) {
		if (response instanceof Pair) {
			me = (Pair) response;
		} else if (response instanceof Map) {
			pairsList = (Map<UUID, Pair>) response;
		}
	}

	public static void main(String[] args) {
		Client client = null;
		Client client2 = null;
		try {
			client = new Client(InetAddress.getByName(args[0]),
					Integer.valueOf(args[1]), Integer.valueOf(args[2]), args[3]);
			client2 = new Client(InetAddress.getByName(args[0]),
					Integer.valueOf(args[1]), Integer.valueOf(args[2]), args[3]);
		} catch (NumberFormatException | UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			client.send(new ConnectionRequest(client.me));
			client2.send(new ConnectionRequest(client.me));
			System.out.println(client.me);
			System.out.println(client2.me);
			client.send(new PairListRequest());
			client2.send(new PairListRequest());
			System.out.println(client.pairsList);
			System.out.println(client2.pairsList);
			client.send(new QuitRequest(client.me));
			client2.send(new PairListRequest());
			System.out.println(client2.pairsList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<File> getFilesToShared(String path) {
		File files = new File(path);
		return Arrays.asList(files.listFiles());
	}

	private void close() {
		socket.close();
	}
}

class FilesSharedSocket extends ServerSocket implements Runnable {

	public FilesSharedSocket() throws IOException {
		super(0);
		System.out.println("FilesSharedSocket created on port "+this.getLocalPort());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket newClient = null;
		try {
			newClient = this.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}



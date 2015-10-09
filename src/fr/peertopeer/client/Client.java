package fr.peertopeer.client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.peertopeer.objects.Pair;
import fr.peertopeer.objects.request.ConnectionRequest;
import fr.peertopeer.objects.request.PairListRequest;
import fr.peertopeer.objects.request.Request;
import fr.peertopeer.utils.Serializer;

public class Client {

	private Map<UUID, Pair> pairsList;
	private Pair me;
	private DatagramSocket socket;
	protected InetAddress serverAdress;
	protected int serverPort;
	private byte[] bufferReceived;
	private FilesSharedSocket filesSSocket = null;
	private ThreadRefresh threadRefresh;
	private ThreadListen threadListen;

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
			System.err.println(e.getMessage());
		}
		me = new Pair(socket.getLocalAddress(), socket.getLocalPort(),
				filesSSocket.getLocalPort(), getFilesToShared(pathSharedFiles));

		try {
			sendAndReceive(new ConnectionRequest(me));
			sendAndReceive(new PairListRequest());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		threadListen = new ThreadListen(this, socket);
		threadListen.start();
		
		//threadRefresh = new ThreadRefresh(this, socket);
		//threadRefresh.start();
	}

	public void send(Request request) throws IOException {
		// On Serialize la Request
		byte[] datas = Serializer.serialize(request);

		// On envoie la Request serialized
		DatagramPacket packet = new DatagramPacket(datas, datas.length,
				serverAdress, serverPort);
		socket.send(packet);

	}

	public void sendAndReceive(Request request) throws IOException {
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
	protected void receiveResponse(Object response) {
		if (response instanceof Pair) {
			me = (Pair) response;
		} else if (response instanceof Map) {
			pairsList = (Map<UUID, Pair>) response;
		}

		System.out.println("RESPONSE :" + response.getClass() + " | DATA :["
				+ response + "]");
	}

	public static void main(String[] args) {
		try {
			new Client(InetAddress.getByName(args[0]),
					Integer.valueOf(args[1]), Integer.valueOf(args[2]), args[3]);
		} catch (NumberFormatException | UnknownHostException e) {
			System.err.println(e.getMessage());
		}
	}

	private List<File> getFilesToShared(String path) {
		File files = new File(path);
		return Arrays.asList(files.listFiles());
	}

	private void close() {
		socket.close();
		//threadRefresh.interrupt();
		threadListen.interrupt();
	}
}

class FilesSharedSocket extends ServerSocket implements Runnable {

	public FilesSharedSocket() throws IOException {
		super(0);
		System.out.println("FilesSharedSocket created on port "
				+ this.getLocalPort());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket newClient = null;
		try {
			newClient = this.accept();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

}

class ThreadListen extends Thread {

	private Client client;
	private DatagramSocket socket;
	private byte[] bufferReceived;

	public ThreadListen(Client client, DatagramSocket socket) {
		this.client = client;
		this.socket = socket;

		try {
			bufferReceived = new byte[socket.getReceiveBufferSize()];
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void run() {
		while (!isInterrupted()) {

			// On ecoute la reponse
			DatagramPacket rPacket = new DatagramPacket(bufferReceived,
					bufferReceived.length);
			try {
				socket.receive(rPacket);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}

			// On traite la reponse
			client.receiveResponse(Serializer.deserialize(rPacket.getData()));

		}
	}
}

class ThreadRefresh extends Thread {

	private Client client;
	private DatagramSocket socket;
	private byte[] bufferReceived;

	public ThreadRefresh(Client client, DatagramSocket socket) {
		this.client = client;
		this.socket = socket;

		try {
			bufferReceived = new byte[socket.getReceiveBufferSize()];
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				// On Serialize la Request
				byte[] datas = Serializer.serialize(new PairListRequest());

				// On envoie la Request serialized
				DatagramPacket packet = new DatagramPacket(datas, datas.length,
						client.serverAdress, client.serverPort);
				socket.send(packet);

				// On ecoute la reponse
				DatagramPacket rPacket = new DatagramPacket(bufferReceived,
						bufferReceived.length);
				socket.receive(rPacket);

				// On traite la reponse
				client.receiveResponse(Serializer.deserialize(rPacket.getData()));

				sleep(2000);
			} catch (IOException | InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}

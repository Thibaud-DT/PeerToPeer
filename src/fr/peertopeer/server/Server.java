package fr.peertopeer.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.peertopeer.objects.*;
import fr.peertopeer.objects.request.NewPairRequest;
import fr.peertopeer.objects.request.PairListRequest;
import fr.peertopeer.objects.request.Request;
import fr.peertopeer.utils.Serializer;

public class Server implements Runnable {
	private List<Pair> pairsList;
	private DatagramSocket socket;
	private Thread runningThread;
	private byte[] bufferReceived;

	public Server(int port) {
		try {
			this.socket = new DatagramSocket(port);
			bufferReceived = new byte[socket.getReceiveBufferSize()];
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pairsList = new ArrayList<Pair>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!runningThread.isInterrupted()) {
			try {
				DatagramPacket packet = new DatagramPacket(bufferReceived, bufferReceived.length);
				socket.receive(packet);
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
				Request req = (Request) iStream.readObject();
				iStream.close();
				if (req instanceof NewPairRequest) {
					Pair newPair = ((NewPairRequest) req).getNewPair();
					newPair(newPair, packet);
				} else if (req instanceof PairListRequest) {
					sendPairList(packet);
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void newPair(Pair newPair, DatagramPacket packet) throws IOException {
		System.out.println("New pair : "+packet.getAddress().getHostName());
		newPair.setUuid(UUID.randomUUID());
		pairsList.add(newPair);
		byte[] echo = Serializer.serialize(newPair);
		packet.setData(echo);
		socket.send(packet);
	}

	private void sendPairList(DatagramPacket packet) throws IOException {
		System.out.println(packet.getAddress().getHostName()+" retrieves pairs list");
		byte[] datas = Serializer.serialize(pairsList);
		packet.setData(datas);
		socket.send(packet);
	}

	public void go() {
		if (runningThread == null || !runningThread.isAlive()) {
			runningThread = new Thread(this);
			runningThread.start();
		} else {
			System.err.println("/!\\ Server is already running");
			return;
		}
		System.out.println("Server started");
	}

	public void stop() {
		if (runningThread.isAlive() && !runningThread.isInterrupted())
			runningThread.interrupt();
		else {
			System.err.println("/!\\ Server is stopped or being stopped");
			return;
		}
		System.out.println("Server stopping...");
	}
	
	public static void main(String[] args) {
		Server server = new Server(Integer.valueOf(args[0]));
		server.go();
	}

}

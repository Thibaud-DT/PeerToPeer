package fr.peertopeer.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import fr.peertopeer.objects.Pair;
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
		while (!runningThread.isInterrupted()) {
			try {
				DatagramPacket packet = new DatagramPacket(bufferReceived, bufferReceived.length);
				socket.receive(packet);
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
				Request req = (Request) iStream.readObject();
				iStream.close();
				send(req, packet);
			} catch (IOException | ClassNotFoundException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	private void send(Request request, DatagramPacket packet) throws IOException{
		byte[] datas = Serializer.serialize(request.build(this));
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
	
	public void addPair(Pair newPair) {
		pairsList.add(newPair);
	}
	
	public List<Pair> getPairsList(){
		return pairsList;
	}

	public static void main(String[] args) {
		Server server = new Server(Integer.valueOf(args[0]));
		server.go();
	}
}

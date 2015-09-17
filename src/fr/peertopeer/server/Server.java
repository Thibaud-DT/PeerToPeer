package fr.peertopeer.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.UUID;

import fr.peertopeer.objects.NewPair;
import fr.peertopeer.objects.Pair;
import fr.peertopeer.objects.Request;

public class Server implements Runnable {
	private List<Pair> pairsList;
	private DatagramSocket socket;
	private Thread runningThread;

	public Server(int port) {
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!runningThread.isInterrupted()) {
			try {
				DatagramPacket packet = new DatagramPacket(new byte[socket.getReceiveBufferSize()], socket.getReceiveBufferSize());
				socket.receive(packet);
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
				Request req = (Request)iStream.readObject();
				iStream.close();
				if(req instanceof NewPair) {
					Pair newPair = ((NewPair)req).getNewPair();
					newPair.setUuid(UUID.randomUUID());
					
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

	public void go() {
		if (runningThread == null && !runningThread.isAlive()) {
			runningThread = new Thread(this);
			runningThread.start();
		} else System.err.println("/!\\ Server is already running");
	}

	public void stop() {
		if(runningThread.isAlive() && !runningThread.isInterrupted())
			runningThread.interrupt();
		else System.err.println("/!\\ Server is stopped or being stopped");
	}

}

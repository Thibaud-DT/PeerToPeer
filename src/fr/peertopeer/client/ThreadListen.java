package fr.peertopeer.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import fr.peertopeer.utils.Serializer;

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
			DatagramPacket rPacket = new DatagramPacket(bufferReceived, bufferReceived.length);
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
package fr.peertopeer.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import fr.peertopeer.objects.request.PairListRequest;
import fr.peertopeer.utils.Serializer;

public class ThreadRefresh extends Thread {

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
				DatagramPacket packet = new DatagramPacket(datas, datas.length, client.serverAdress, client.serverPort);
				socket.send(packet);

				// On ecoute la reponse
				DatagramPacket rPacket = new DatagramPacket(bufferReceived, bufferReceived.length);
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
package fr.peertopeer.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.peertopeer.objects.Pair;
import fr.peertopeer.objects.request.Request;
import fr.peertopeer.utils.Logger;
import fr.peertopeer.utils.Serializer;

public class Server implements Runnable {
	
	private Logger logger = Logger.getInstance();
	
	private Map<UUID,Pair> pairsList;
	private DatagramSocket socket;
	private Thread runningThread;
	private byte[] bufferReceived;

	public Server(int port) {
		try {
			this.socket = new DatagramSocket(port);
			bufferReceived = new byte[socket.getReceiveBufferSize()];
		} catch (SocketException e) {
			logger.error(e.getMessage());
		}
		pairsList = new HashMap<UUID,Pair>();
	}

	@Override
	public void run() {
		while (!runningThread.isInterrupted()) {
			try {
				DatagramPacket packet = new DatagramPacket(bufferReceived, bufferReceived.length);
				socket.receive(packet);
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
				Request req = (Request) iStream.readObject();
//				iStream.close();
//				if(req.isBroadcast()){
//					req.build(this);
//					DatagramPacket packetPair = new DatagramPacket(packet.getData(), packet.getLength());
//					for(Entry<UUID,Pair> pair : pairsList.entrySet()){
//						packetPair.setAddress(pair.getValue().getAdress());
//						packetPair.setPort(pair.getValue().getPort());
//						send(req, packetPair);
//					}
//				}else{
					send(req, packet);					
//				}
			} catch (IOException | ClassNotFoundException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	private void send(Request request, DatagramPacket packet) throws IOException{
		Object response = request.build(this);
		byte[] datas = Serializer.serialize(response);
		logger.debug("REQUEST :["+request.getClass()+"] | FROM :["+packet.getAddress()+":"+packet.getPort()+"] | RESPONSE :["+response+"]");  
		packet.setData(datas);
		socket.send(packet);
	}
	
//	public void sendBroadcast(Request request) throws IOException{
//		request.build(this);
//		DatagramPacket packetPair = new DatagramPacket(bufferReceived, bufferReceived.length);
//		for(Entry<UUID,Pair> pair : pairsList.entrySet()){
//			packetPair.setAddress(pair.getValue().getAdress());
//			packetPair.setPort(pair.getValue().getPort());
//			send(request, packetPair);
//		}
//	}

	public void go() {
		if (runningThread == null || !runningThread.isAlive()) {
			runningThread = new Thread(this);
			runningThread.start();
		} else {
			logger.error("/!\\ Server is already running !");
			return;
		}
		logger.success("Server started");
	}

	public void stop() {
		if (runningThread.isAlive() && !runningThread.isInterrupted())
			runningThread.interrupt();
		else {
			logger.error("/!\\ Server is already stoped !");
			return;
		}
		logger.success("Server stopping..");
	}
	
	public void addPair(Pair newPair) {
		pairsList.put(newPair.getUuid(),newPair);
	}
	
	public void removePair(Pair removePair){
		pairsList.remove(removePair.getUuid());
	}
	
	public Map<UUID,Pair> getPairsList(){
		return pairsList;
	}
}

package fr.peertopeer.client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import fr.peertopeer.objects.Pair;
import fr.peertopeer.objects.request.ConnectionRequest;
import fr.peertopeer.objects.request.PairListRequest;
import fr.peertopeer.objects.request.Request;
import fr.peertopeer.utils.Logger;
import fr.peertopeer.utils.Serializer;

public class Client {
	
	private Logger logger = Logger.getInstance();

	private Map<UUID, Pair> pairsList;
	private Pair me;
	
	private DatagramSocket socketServer;
	private DatagramSocket socketClient;
	
	protected InetAddress serverAdress;
	protected int serverPort;
	
	private byte[] bufferReceived;
	
	private FilesSharedSocket filesSSocket = null;
	private ThreadRefresh threadRefresh;
	private ThreadListen threadListen;

	public Client(InetAddress serverAdress, int serverPort, String pathSharedFiles) {
		try {
			socketServer = new DatagramSocket();
			bufferReceived = new byte[socketServer.getReceiveBufferSize()];
		} catch (SocketException e) {
			logger.error(e.getMessage());
		}
		
		this.serverPort = serverPort;
		this.serverAdress = serverAdress;
		
		me = new Pair(socketServer.getLocalAddress(), socketServer.getLocalPort(), 0, getFilesToShared(pathSharedFiles));

		try {
			filesSSocket = new FilesSharedSocket(this.me.getSharedFiles());
			this.me.setFilePort(filesSSocket.getLocalPort());
			logger.debug("Client FilesServerSocket Port :"+filesSSocket.getLocalPort());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		try {
			sendAndReceive(new ConnectionRequest(me));
			sendAndReceive(new PairListRequest());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		threadRefresh = new ThreadRefresh(this, socketServer);
		threadRefresh.start();
		threadListen = new ThreadListen(this, socketClient);
		threadListen.start();
	}

	public Pair getMe() {
		return me;
	}

	public void setMe(Pair me) {
		this.me = me;
	}

	public void send(Request request) throws IOException {
		byte[] datas = Serializer.serialize(request);

		DatagramPacket packet = new DatagramPacket(datas, datas.length, serverAdress, serverPort);
		socketServer.send(packet);
	}

	public void sendAndReceive(Request request) throws IOException {
		byte[] datas = Serializer.serialize(request);

		DatagramPacket packet = new DatagramPacket(datas, datas.length, serverAdress, serverPort);
		socketServer.send(packet);

		DatagramPacket rPacket = new DatagramPacket(bufferReceived, bufferReceived.length);
		socketServer.receive(rPacket);

		receiveResponse(Serializer.deserialize(rPacket.getData()));
	}

	@SuppressWarnings("unchecked")
	protected void receiveResponse(Object response) {
		if (response instanceof Pair) {
			me = (Pair) response;
		} else if (response instanceof Map) {
			pairsList = (Map<UUID, Pair>) response;
			pairsList.remove(this.me.getUuid());
		}

		logger.debug("RESPONSE :" + response.getClass() + " | DATA :[" + response + "]");
	}

	


	public void downloadFile(Pair pair, File file) {
		if (!pairsList.isEmpty()) {
			for (Entry<UUID, Pair> e : pairsList.entrySet()) {
				new DownloadFile(e.getValue().getSharedFiles().get(0), e.getValue(), "/home/chavalc/").start();
			}
		}
	}
	
	
	
	private List<File> getFilesToShared(String path) {
		File files = new File(path);
		return Arrays.asList(files.listFiles());
	}

	public void close() {
		threadListen.interrupt();
		threadRefresh.interrupt();
		socketServer.close();
		try {
			filesSSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}







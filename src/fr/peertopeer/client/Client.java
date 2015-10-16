package fr.peertopeer.client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.peertopeer.objects.FileShared;
import fr.peertopeer.objects.Pair;
import fr.peertopeer.objects.request.ConnectionRequest;
import fr.peertopeer.objects.request.FileRequest;
import fr.peertopeer.objects.request.PairListRequest;
import fr.peertopeer.objects.request.QuitRequest;
import fr.peertopeer.objects.request.Request;
import fr.peertopeer.utils.Logger;
import fr.peertopeer.utils.Serializer;

public class Client {
	
	private Logger logger = Logger.getInstance();

	public Map<UUID, Pair> getPairsList() {
		return pairsList;
	}

	public void setPairsList(Map<UUID, Pair> pairsList) {
		this.pairsList = pairsList;
	}

	private Map<UUID, Pair> pairsList;
	private Pair me;
	
	private DatagramSocket socketServer;
	private DatagramSocket socketClient;
	
	protected InetAddress serverAdress;
	protected int serverPort;
	
	private byte[] bufferReceived;
	
	private ThreadRefresh threadRefresh;
	private ThreadListen threadListen;

	private String pathSharedFiles;
	private List<File> filesShared;

	public Client(InetAddress serverAdress, int serverPort, String pathSharedFiles) {
		try {
			socketServer = new DatagramSocket();
			socketClient = new DatagramSocket();
			bufferReceived = new byte[socketServer.getReceiveBufferSize()];
		} catch (SocketException e) {
			logger.error(e.getMessage());
		}
		
		this.serverPort = serverPort;
		this.serverAdress = serverAdress;
		
		this.pathSharedFiles = pathSharedFiles;
		logger.debug(this.pathSharedFiles);
		filesShared = getFilesToShared(pathSharedFiles);
		
		me = new Pair(socketServer.getLocalAddress(), socketServer.getLocalPort(), socketClient.getLocalPort(), filesShared);

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

	public List<File> getFilesShared() {
		return filesShared;
	}

	public void setFilesShared(List<File> filesShared) {
		this.filesShared = filesShared;
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
		} else if (response instanceof FileRequest){
			FileRequest Fresponse = (FileRequest) response;
			FileShared sendResponse = (FileShared)Fresponse.build(this);
			this.send(Fresponse.getRequester(), sendResponse);
		} else if (response instanceof FileShared){
			new ThreadReceiveFile(this,(FileShared)response).start();
		}

		logger.debug("RESPONSE :" + response.getClass() + " | DATA :[" + response + "]");
	}


	private void send(Pair receiver,Object object) {
		byte[] datas = Serializer.serialize(object);

		DatagramPacket packet = new DatagramPacket(datas, datas.length, receiver.getAdress(), receiver.getClientPort());
		
		try{
			socketClient.send(packet);
		}catch(IOException e){
			logger.error(e.getMessage());
		}
		
	}

	public void sendFileRequest(UUID uuid, String filename){
		send(pairsList.get(uuid), new FileRequest(this.me, filename));
	}
	
	public List<File> getFilesToShared(String path) {
		File dir = new File(path);
		if(dir.exists())
			return Arrays.asList(dir.listFiles());
		else{
			dir.mkdirs();
			logger.warning("Le dossier ["+path+"] n'exite pas, il a été créé.");
			return new ArrayList<File>();
		}
			
	}

	public String getPathSharedFiles() {
		return pathSharedFiles;
	}

	public void setPathSharedFiles(String pathSharedFiles) {
		this.pathSharedFiles = pathSharedFiles;
	}
	
	public void close(){
		try {
			send(new QuitRequest(getMe()));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		socketClient.close();
		socketServer.close();
		threadListen.interrupt();
		threadRefresh.interrupt();
	}
}
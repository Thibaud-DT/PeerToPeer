package fr.peertopeer.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import fr.peertopeer.objects.Pair;
import fr.peertopeer.objects.request.ConnectionRequest;
import fr.peertopeer.objects.request.PairListRequest;
import fr.peertopeer.objects.request.Request;
import fr.peertopeer.utils.Serializer;
import sun.awt.FwDispatcher;

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
	private Thread threadServerSocket;

	public Client(InetAddress serverAdress, int serverPort, int tcpSharedFilesPort, String pathSharedFiles) {
		try {
			socket = new DatagramSocket();
			bufferReceived = new byte[socket.getReceiveBufferSize()];
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serverPort = serverPort;
		this.serverAdress = serverAdress;
		me = new Pair(socket.getLocalAddress(), socket.getLocalPort(), 0, getFilesToShared(pathSharedFiles));

		threadListen = new ThreadListen(this, socket);
		threadListen.start();

		try {
			FilesSharedSocket serverSocket = new FilesSharedSocket(this.me.getSharedFiles());
			this.me.setFilePort(serverSocket.getLocalPort());
			System.out.println(serverSocket.getLocalPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			sendAndReceive(new ConnectionRequest(me));
			sendAndReceive(new PairListRequest());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		// threadRefresh = new ThreadRefresh(this, socket);
		// threadRefresh.start();
	}

	public void send(Request request) throws IOException {
		// On Serialize la Request
		byte[] datas = Serializer.serialize(request);

		// On envoie la Request serialized
		DatagramPacket packet = new DatagramPacket(datas, datas.length, serverAdress, serverPort);
		socket.send(packet);
	}

	public void sendAndReceive(Request request) throws IOException {
		// On Serialize la Request
		byte[] datas = Serializer.serialize(request);

		// On envoie la Request serialized
		DatagramPacket packet = new DatagramPacket(datas, datas.length, serverAdress, serverPort);
		socket.send(packet);

		// On ecoute la reponse
		DatagramPacket rPacket = new DatagramPacket(bufferReceived, bufferReceived.length);
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
			pairsList.remove(this.me.getUuid());
		}

		System.out.println("RESPONSE :" + response.getClass() + " | DATA :[" + response + "]");
	}

	public static void main(String[] args) {
		try {
			Client cli = new Client(InetAddress.getByName(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]),
					args[3]);
			Thread.sleep(1000);
			cli.downloadFile(null, null);
		} catch (NumberFormatException | UnknownHostException e) {
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
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
		// threadRefresh.interrupt();
		threadListen.interrupt();
	}

	public void downloadFile(Pair pair, File file) {
		if (!pairsList.isEmpty()) {
			System.out.println("Try to downloading file...");
			for (Entry<UUID, Pair> e : pairsList.entrySet()) {
				new DownloadFile(e.getValue().getSharedFiles().get(0), e.getValue(), "/home/chavalc/").start();
			}
		}
	}
}

class DownloadFile extends Thread {
	private File dwnFile;
	private Pair pair;
	private String pathDest;

	public DownloadFile(File dwnFile, Pair pair, String pathDest) {
		// TODO Auto-generated constructor stub
		this.dwnFile = dwnFile;
		this.pair = pair;
		this.pathDest = pathDest;
	}

	@Override
	public void run() {
		System.out.println("Downloading file " + dwnFile.getName() + " (on +"+pair.getAdress().getHostName()+":"+pair.getFilePort()+")...");
		Socket sock = null;
		try {
			sock = new Socket(pair.getAdress(), pair.getFilePort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		DataInputStream dis = null;
		DataOutputStream dos = null;
		try {
			dis = new DataInputStream(sock.getInputStream());
			dos = new DataOutputStream(sock.getOutputStream());
			receiveFile(dos, dis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	private void receiveFile(DataOutputStream out, DataInputStream in) throws IOException {
		out.write(dwnFile.getName().getBytes());
		FileWriter ofile = new FileWriter(this.pathDest+""+this.dwnFile.getName()+".p2p");
		for(int i = 0; i < this.dwnFile.length(); i++) {
			byte b;
			try {
				b = in.readByte();
			} catch(SocketException e) {
				e.printStackTrace();
				return;
			}
			ofile.write(b);
			System.out.println(i*100/this.dwnFile.length()+"%");
		}
	}
}

class FilesSharedSocket extends ServerSocket implements Runnable {

	private List<File> files;

	public FilesSharedSocket(List<File> sharedFiles) throws IOException {
		super(0);
		files = sharedFiles;
		System.out.println("FilesSharedSocket created on port " + this.getLocalPort());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket newClient = null;
		while (true) {
			try {
				InputStream in;
				OutputStream out;
				Scanner scan;
				newClient = this.accept();
				System.out.println("A pair is connected");
				System.out.flush();
				in = newClient.getInputStream();
				out = newClient.getOutputStream();
				scan = new Scanner(in);
				String sfile = scan.nextLine();
				System.out.println("A client wants to download " + sfile);
				File file = isSharedFile(sfile);
				if (file != null) {
					sendFile(out, file);
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

	}

	private File isSharedFile(String file) {
		for (File f : files) {
			if (f.getName().equals(file)) {
				return f;
			}
		}
		return null;
	}

	private void sendFile(OutputStream out, File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		byte[] buffer = new byte[(int) file.length()];
		try {
			DataOutputStream dos = new DataOutputStream(out);
			fis.read(buffer);
			out.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
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

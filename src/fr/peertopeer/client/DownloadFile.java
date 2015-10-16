package fr.peertopeer.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import fr.peertopeer.objects.Pair;

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
		System.out.println("Downloading file " + dwnFile.getName() + "...");
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
		
		ofile.close();
	}
}
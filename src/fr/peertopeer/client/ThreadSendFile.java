package fr.peertopeer.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import fr.peertopeer.utils.Logger;

public class ThreadSendFile extends Thread {

	private String filename;
	private Client sender;
	private static Logger logger = Logger.getInstance();
	private ServerSocket server;

	public ThreadSendFile(Client sender, String filename) throws IOException {
		this.filename = filename;
		this.sender = sender;
		server= new ServerSocket(0);
		logger.debug("Server Send Create on "+server.getInetAddress()+":"+server.getLocalPort());
	}

	public int getSharedPort() {
		return server.getLocalPort();
	}

	@Override
	public void run() {
		Socket socket = null;
		try {
			OutputStream out;
			socket = server.accept();
			logger.info("Pair connected");
			logger.debug("Start Send File");
			out = socket.getOutputStream();
			File file = getFile(filename);
			if (file != null) {
				sendFile(out, file);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		this.interrupt();
		logger.debug("ThreadSend for "+filename+" is closed.");
	}

	private File getFile(String file) {
		for (File f : sender.getFilesShared()) {
			if (f.getName().equals(file)) {
				return f;
			}
		}
		return null;
	}

	private void sendFile(OutputStream out, File file) {
		FileInputStream fis = null;
		DataOutputStream dos = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];

			dos = new DataOutputStream(out);

			fis.read(buffer);
			out.write(buffer);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return;
		} finally {
			try {
				fis.close();
				dos.close();
			} catch (IOException e1) {
				logger.error(e1.getMessage());
			}
		}
	}

}
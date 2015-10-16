package fr.peertopeer.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

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
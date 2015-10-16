package fr.peertopeer.objects;

import java.io.Serializable;

public class FileShared implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private Pair other;
	private int portShared;
	
	
	@Override
	public String toString() {
		return "FileShared [fileName=" + fileName + ", other=" + other
				+ ", portShared=" + portShared + "]";
	}


	public FileShared(Pair other, int portShared, String filename) {
		this.other = other;
		this.fileName = filename;
		this.portShared = portShared;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public Pair getOther() {
		return other;
	}


	public void setOther(Pair other) {
		this.other = other;
	}


	public int getPortShared() {
		return portShared;
	}


	public void setPortShared(int portShared) {
		this.portShared = portShared;
	}

}

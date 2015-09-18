package fr.peertopeer.objects;

import java.io.Serializable;

public class NewPair extends Request implements Serializable{
	private Pair newPair;
	
	public NewPair() {
	}
	
	public NewPair(Pair newPair) {
		this.newPair = newPair;
	}

	public Pair getNewPair() {
		return newPair;
	}

	public void setNewPair(Pair newPair) {
		this.newPair = newPair;
	}
}

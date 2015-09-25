package fr.peertopeer.objects.request;

import java.io.Serializable;

import fr.peertopeer.objects.Pair;

public class NewPairRequest extends Request implements Serializable{
	private Pair newPair;
	
	public NewPairRequest() {
	}
	
	public NewPairRequest(Pair newPair) {
		this.newPair = newPair;
	}

	public Pair getNewPair() {
		return newPair;
	}

	public void setNewPair(Pair newPair) {
		this.newPair = newPair;
	}
}

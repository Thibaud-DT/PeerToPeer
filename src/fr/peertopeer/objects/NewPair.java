package fr.peertopeer.objects;

public class NewPair extends Request {
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

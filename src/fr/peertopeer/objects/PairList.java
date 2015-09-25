package fr.peertopeer.objects;

import java.io.Serializable;
import java.util.List;

public class PairList implements Serializable{

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "PairList [pairs=" + pairs + "]";
	}

	private List<Pair> pairs;

	public PairList() {
		
	}
	
	public PairList(List<Pair> pairs) {
		this.pairs = pairs;
	}
	public List<Pair> getPairs() {
		return pairs;
	}

	public void setPairs(List<Pair> pairs) {
		this.pairs = pairs;
	}
}

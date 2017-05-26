package utils;

public class Pair<A, B> {

	private A objectOne;
	
	private B objectTwo;
	
	public Pair(A objectOne, B objectTwo) {
		this.objectOne = objectOne;
		this.objectTwo = objectTwo;
	}
	
	public A getFirst() {
		return objectOne;
	}
	
	public B getSecond() {
		return objectTwo;
	}
	
}

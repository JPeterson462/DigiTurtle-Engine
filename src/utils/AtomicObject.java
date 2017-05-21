package utils;

public class AtomicObject<T> {

	private T value;
	
	public void set(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
}

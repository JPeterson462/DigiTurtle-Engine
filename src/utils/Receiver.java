package utils;

@FunctionalInterface
public interface Receiver<T> {

	public void receive(T object);
	
}

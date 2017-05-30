package engine.networking;

import java.util.HashMap;

public abstract class Endpoint {

	private HashMap<Class<?>, Decoder<?>> decoders = new HashMap<>();
	
	private HashMap<Class<?>, Encoder<?>> encoders = new HashMap<>();
	
	private Registry registry = new Registry();
	
	public Registry getRegistry() {
		return registry;
	}
	
	public void register(Coder<?> coder) {
		decoders.put(coder.getType(), coder);
		encoders.put(coder.getType(), coder);
	}
	
	public void register(Decoder<?> coder) {
		decoders.put(coder.getType(), coder);
	}
	
	public void register(Encoder<?> coder) {
		encoders.put(coder.getType(), coder);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Decoder<T> getDecoder(Class<T> type) {
		while (!decoders.containsKey(type)) {
			type = (Class<T>) type.getSuperclass();
		}
		return (Decoder<T>) decoders.get(type);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Decoder<T> getDecoder(int id) {
		Class<T> type = (Class<T>) registry.lookup(id);
		return getDecoder(type);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Encoder<T> getEncoder(Class<T> type) {
		while (!encoders.containsKey(type)) {
			type = (Class<T>) type.getSuperclass();
		}
		return (Encoder<T>) encoders.get(type);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Encoder<T> getEncoder(int id) {
		Class<T> type = (Class<T>) registry.lookup(id);
		return getEncoder(type);
	}
	
	public abstract void write(Object object);

}

package engine.networking;

@FunctionalInterface
public interface EndpointListener<T> {

	public void onPacketReceived(T object, Endpoint endpoint, WriteFunction writeFunction);
	
}

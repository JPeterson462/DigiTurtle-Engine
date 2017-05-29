package engine.networking;

@FunctionalInterface
public interface EndpointListener {

	public void onPacketReceived(Object object, Endpoint endpoint, WriteFunction writeFunction);
	
}

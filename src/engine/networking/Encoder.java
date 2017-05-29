package engine.networking;

import java.nio.ByteBuffer;

public interface Encoder<T> {

	public ByteBuffer encode(T object);
	
	public Class<T> getType();
	
}

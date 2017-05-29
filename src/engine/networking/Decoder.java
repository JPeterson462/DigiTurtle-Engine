package engine.networking;

import java.nio.ByteBuffer;

public interface Decoder<T> {

	public T decode(ByteBuffer buffer);
	
	public Class<T> getType();
	
}

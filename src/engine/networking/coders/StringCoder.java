package engine.networking.coders;

import java.nio.ByteBuffer;

import engine.networking.Coder;

public class StringCoder implements Coder<String> {

	@Override
	public String decode(ByteBuffer buffer) {
		buffer.rewind();
		byte[] data = new byte[buffer.limit()];
		buffer.get(data);
		return new String(data);
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	public ByteBuffer encode(String object) {
		return (ByteBuffer) ByteBuffer.wrap(object.getBytes()).position(0);
	}

}

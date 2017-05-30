package engine.networking.coders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import engine.networking.Coder;
import utils.ByteBufferInputStream;

public class SerializedCoder implements Coder<Object> {
	
	@Override
	public Object decode(ByteBuffer buffer) {
		try {
			ObjectInputStream stream = new ObjectInputStream(new ByteBufferInputStream(buffer));
			Object object = stream.readObject();
			stream.close();
			return object;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<Object> getType() {
		return Object.class;
	}

	@Override
	public ByteBuffer encode(Object object) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream stream = new ObjectOutputStream(byteStream);
			stream.writeObject(object);
			stream.flush();
			stream.close();
			byte[] bytes = byteStream.toByteArray();
			ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
			buffer.put(bytes).flip();
			return buffer;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

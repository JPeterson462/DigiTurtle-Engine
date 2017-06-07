package utils.io;

import java.io.IOException;
import java.io.InputStream;

public class ReadCapsule implements Readable {
	
	private InputStream stream;
	
	private byte[] chunk = new byte[Capsule.BUFFER_SIZE];
	
	private int bytesAvailable, position;
	
	public void setSource(InputStream stream) {
		this.stream = stream;
		bytesAvailable = 0;
		position = 0;
	}
	
	private void readBuffer() {
		byte[] buffer = new byte[Capsule.BUFFER_SIZE - bytesAvailable];
		try {
			if (bytesAvailable == Capsule.BUFFER_SIZE) {
				return; // No Data
			}
			int read = stream.read(buffer);
			if (read < 0) {
				return; // No Data
			}
			System.arraycopy(chunk, position, chunk, 0, bytesAvailable);
			System.arraycopy(buffer, 0, chunk, bytesAvailable, read);
			position = 0;
			bytesAvailable += read;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void bytesRead(int bytes) {
		position += bytes;
		bytesAvailable -= bytes;
	}

	@Override
	public byte readByte() {
		readBuffer();
		byte b = Bytes.getByte(chunk, position);
		bytesRead(Capsule.BYTE_SIZE);
		return b;
	}

	@Override
	public char readChar() {
		readBuffer();
		char c = Bytes.getChar(chunk, position);
		bytesRead(Capsule.CHAR_SIZE);
		return c;
	}

	@Override
	public short readShort() {
		readBuffer();
		short s = Bytes.getShort(chunk, position);
		bytesRead(Capsule.SHORT_SIZE);
		return s;
	}

	@Override
	public int readInt() {
		readBuffer();
		int i = Bytes.getInt(chunk, position);
		bytesRead(Capsule.INT_SIZE);
		return i;
	}

	@Override
	public float readFloat() {
		readBuffer();
		float f = Bytes.getFloat(chunk, position);
		bytesRead(Capsule.FLOAT_SIZE);
		return f;
	}

	@Override
	public long readLong() {
		readBuffer();
		long l = Bytes.getLong(chunk, position);
		bytesRead(Capsule.LONG_SIZE);
		return l;
	}

	@Override
	public double readDouble() {
		readBuffer();
		double d = Bytes.getDouble(chunk, position);
		bytesRead(Capsule.DOUBLE_SIZE);
		return d;
	}

	@Override
	public String readString() {
		int size = readInt();
		char[] buffer = new char[size];
		for (int i = 0; i < size; i++) {
			buffer[i] = readChar();
		}
		return new String(buffer);
	}

}

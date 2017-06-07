package utils.io;

import java.io.IOException;
import java.io.OutputStream;

public class WriteCapsule implements Writable {
	
	private OutputStream stream;
	
	private int bytesWritten;
	
	private byte[] buffer = new byte[Capsule.BUFFER_SIZE];

	public void setSource(OutputStream stream) {
		this.stream = stream;
		bytesWritten = 0;
	}
	
	private void tryFlush() {
		if (bytesWritten > Capsule.BUFFER_SIZE) {
			try {
				stream.write(buffer, 0, bytesWritten);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			flush();
		}
	}
	
	private void bytesWritten(int bytes) {
		bytesWritten += bytes;
	}
	
	public void flush() {
		try {
			stream.flush();
			bytesWritten = 0;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void writeByte(byte b) {
		Bytes.putByte(buffer, bytesWritten, b);
		bytesWritten(Capsule.BYTE_SIZE);
		tryFlush();
	}

	@Override
	public void writeChar(char c) {
		Bytes.putChar(buffer, bytesWritten, c);
		bytesWritten(Capsule.CHAR_SIZE);
		tryFlush();		
	}

	@Override
	public void writeShort(short s) {
		Bytes.putShort(buffer, bytesWritten, s);
		bytesWritten(Capsule.SHORT_SIZE);		
		tryFlush();		
	}

	@Override
	public void writeInt(int i) {
		Bytes.putInt(buffer, bytesWritten, i);
		bytesWritten(Capsule.INT_SIZE);		
		tryFlush();
	}

	@Override
	public void writeFloat(float f) {
		Bytes.putFloat(buffer, bytesWritten, f);
		bytesWritten(Capsule.FLOAT_SIZE);
		tryFlush();		
	}

	@Override
	public void writeLong(long l) {
		Bytes.putLong(buffer, bytesWritten, l);
		bytesWritten(Capsule.LONG_SIZE);
		tryFlush();
	}

	@Override
	public void writeDouble(double d) {
		Bytes.putDouble(buffer, bytesWritten, d);
		bytesWritten(Capsule.DOUBLE_SIZE);
		tryFlush();
	}

	@Override
	public void writeString(String string) {
		char[] data = string.toCharArray();
		writeInt(data.length);
		for (int i = 0; i < data.length; i++) {
			writeChar(data[i]);
		}
		tryFlush();
	}
	
}

package utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {
	
	private ByteBuffer source;
	
	public ByteBufferInputStream(ByteBuffer source) {
		this.source = source;
	}
	
	public void close() {
		
	}
	
	public int available() {
		return source.remaining();
	}
	
	public boolean markSupported() {
		return true;
	}
	
	public void mark(int readLimit) {
		source.mark();
	}
	
	public void reset() {
		source.reset();
	}
	
	public long skip(long n) {
		int newPosition = Math.min((int) n, source.remaining());
		source.position(source.position() + newPosition);
		return (long) newPosition;
	}

	public int read() throws IOException {
		if (!source.hasRemaining())
			return -1;
		return (int) source.get() & 0xFF;
	}
	
	public int read(byte[] bytes, int offset, int length) throws IOException {
		length = Math.min(length, source.remaining());
		if (length == 0)
			return -1;
		source.get(bytes, offset, length);
		return length;
	}

}

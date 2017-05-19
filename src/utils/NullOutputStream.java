package utils;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {

	@Override
	public void write(int b) throws IOException {
		// Ignore write operation, the data doesn't go anywhere
	}
	
	public void write(byte[] b) throws IOException {
		// Ignore
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		// Ignore
	}
	
	public void close() throws IOException {
		// Ignore
	}

}

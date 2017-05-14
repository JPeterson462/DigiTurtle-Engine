package engine;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import com.esotericsoftware.minlog.Log;

public class IOUtils {
	
	private static final int DEFAULT_CHUNK_SIZE = 4096;
	
	public static String readStringQuietly(InputStream in) {
		try {
			return readString(in);
		} catch (IOException e) {
			Log.error("Failed to read text from input stream " + in, e);
			return null;
		}
	}
	
	public static String readString(InputStream in) throws IOException {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			reader.lines().forEach(line -> builder.append(line).append('\n'));
		}
		return builder.toString();
	}
	
	public static ByteBuffer toBuffer(byte[] data) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public static ByteBuffer readBufferQuietly(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyQuietly(in, out, DEFAULT_CHUNK_SIZE);
		return toBuffer(out.toByteArray());
	}
	
	public static ByteBuffer readBuffer(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out, DEFAULT_CHUNK_SIZE);
		return toBuffer(out.toByteArray());
	}
	
	public static ByteBuffer readBufferQuietly(InputStream in, int chunkSize) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyQuietly(in, out, chunkSize);
		return toBuffer(out.toByteArray());
	}
	
	public static ByteBuffer readBuffer(InputStream in, int chunkSize) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out, chunkSize);
		return toBuffer(out.toByteArray());
	}
	
	public static int copyQuietly(InputStream in, OutputStream out) {
		return copyQuietly(in, out, DEFAULT_CHUNK_SIZE);
	}
	
	public static int copy(InputStream in, OutputStream out) throws IOException {
		return copy(in, out, DEFAULT_CHUNK_SIZE);
	}
	
	public static int copyQuietly(InputStream in, OutputStream out, int chunkSize) {
		try {
			return copy(in, out, chunkSize);
		} catch (IOException e) {
			Log.error("Failed to copy from " + in + " to " + out, e);
			return -1;
		}
	}
	
	public static int copy(InputStream in, OutputStream out, int chunkSize) throws IOException {
		byte[] chunk = new byte[chunkSize];
		int read, totalRead = 0;
		while ((read = in.read(chunk)) > 0) {
			out.write(chunk, 0, read);
			totalRead += read;
		}
		return totalRead;
	}

}

package engine;

import java.io.IOException;
import java.io.InputStream;

import utils.RelativeStreamGenerator;

public class AssetInputStream extends InputStream implements RelativeStreamGenerator {

	private InputStream source;
	
	private String path;
	
	public AssetInputStream(String path) {
		this.path = path;
		source = connectToSource(path);
	}
	
	public String getPath() {
		return path;
	}
	
	private InputStream connectToSource(String path) {
		return getClass().getClassLoader().getResourceAsStream("test/" + path);
	}
	
	private String getDirectory() {
		return path.substring(0, path.lastIndexOf('/') + 1);
	}
	
	@Override
	public int read() throws IOException {
		return source.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return source.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return source.read(b, off, len);
	}
	
	@Override
	public void close() throws IOException {
		source.close();
	}

	@Override
	public InputStream getRelativeStream(String path) {
		return new AssetInputStream(getDirectory() + path);
	}

}

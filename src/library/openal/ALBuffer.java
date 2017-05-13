package library.openal;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;

public class ALBuffer implements ALResource {
	
	private int id;
	
	public ALBuffer() {
		id = AL10.alGenBuffers();
	}
	
	public int getID() {
		return id;
	}
	
	public void bufferData(int format, ShortBuffer data, int sampleRate) {
		AL10.alBufferData(id, format, data, sampleRate);
	}
	
	public void bufferData(int format, ByteBuffer data, int sampleRate) {
		AL10.alBufferData(id, format, data, sampleRate);
	}

	public void delete() {
		GL11.glDeleteTextures(id);
	}
	
	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	public boolean equals(Object object) {
		return object instanceof ALBuffer && ((ALBuffer) object).id == id;
	}

}

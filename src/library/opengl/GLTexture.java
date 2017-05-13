package library.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class GLTexture implements GLResource {
	
	private int id, type;
	
	public GLTexture(int type) {
		this.type = type;
		id = GL11.glGenTextures();
	}
	
	public int getID() {
		return id;
	}
	
	public void activeTexture(int unit) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
	}
	
	public void bind() {
		GL11.glBindTexture(type, id);
	}
	
	public void minFilter(int filter) {
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_MIN_FILTER, filter);
	}
	
	public void magFilter(int filter) {
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_MAG_FILTER, filter);
	}
	
	public void wrapS(int wrap) {
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_S, wrap);
	}
	
	public void wrapT(int wrap) {
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_T, wrap);
	}
	
	public void lodBias(float bias) {
		GL11.glTexParameterf(type, GL14.GL_TEXTURE_LOD_BIAS, bias);
	}
	
	public void depthTexImage(int width, int height) {
		texImage(GL11.GL_DEPTH_COMPONENT, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, null, width, height);
	}
	
	public void texImage(ByteBuffer data, int width, int height) {
		int format = 0, size = data.limit() - data.position();
		if (size % 4 == 0) {
			format = GL11.GL_RGBA;
		}
		else if (size % 3 == 0) {
			format = GL11.GL_RGB;
		}
		else {
			throw new IllegalArgumentException("Cannot predict texture format for " + data);
		}
		texImage(format, format, GL11.GL_UNSIGNED_BYTE, data, width, height);
	}
	
	public void texImage(int internalFormat, int format, int dataType, ByteBuffer data, int width, int height) {
		GL11.glTexImage2D(type, 0, internalFormat, width, height, 0, format, dataType, data);
	}
	
	public void generateMipmaps() {
		GL30.glGenerateMipmap(type);
	}
	
	public void unbind() {
		GL11.glBindTexture(type, 0);
	}
	
	public void delete() {
		GL11.glDeleteTextures(id);
	}
	
	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	public boolean equals(Object object) {
		return object instanceof GLTexture && ((GLTexture) object).id == id;
	}

}

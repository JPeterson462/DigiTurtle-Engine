package library.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GLRenderbuffer implements GLResource {
	
	private int id, type;
	
	public GLRenderbuffer(int type) {
		this.type = type;
		id = GL30.glGenRenderbuffers();
	}
	
	public int getID() {
		return id;
	}
	
	public void bind() {
		GL30.glBindRenderbuffer(type, id);
	}

	public void storage(int width, int height, int format) {
		GL30.glRenderbufferStorage(type, format, width, height);
	}
	
	public void depthStorage(int width, int height) {
		GL30.glRenderbufferStorage(type, GL11.GL_DEPTH_COMPONENT, width, height);
	}
	
	public void unbind() {
		GL30.glBindRenderbuffer(type, 0);
	}
	
	public void delete() {
		GL30.glDeleteRenderbuffers(id);
	}

	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	public boolean equals(Object object) {
		return object instanceof GLRenderbuffer && ((GLRenderbuffer) object).id == id;
	}

}

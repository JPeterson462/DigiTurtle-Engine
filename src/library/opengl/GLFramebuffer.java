package library.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class GLFramebuffer implements GLResource {
	
	private int id, type;
	
	public GLFramebuffer(int type) {
		this.type = type;
		id = GL30.glGenFramebuffers();
	}
	
	public int getID() {
		return id;
	}
	
	public void drawBuffers(int count) {
		if (count > 0) {
			int[] buffers = new int[count];
			for (int i = 0; i < count; i++) {
				buffers[i] = GL30.GL_COLOR_ATTACHMENT0 + i;
			}
			GL20.glDrawBuffers(buffers);
		} else {
			GL11.glDrawBuffer(GL11.GL_NONE);
			GL11.glReadBuffer(GL11.GL_NONE);
		}
	}
	
	public void bind(int width, int height) {
		GL30.glBindFramebuffer(type, id);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glViewport(0, 0, width, height);
	}
	
	public void attachColorTexture(int attachment, int texture) {
		GL32.glFramebufferTexture(type, GL30.GL_COLOR_ATTACHMENT0 + attachment, texture, 0);
	}
	
	public void attachDepthTexture(int texture) {
		GL32.glFramebufferTexture(type, GL30.GL_DEPTH_ATTACHMENT, texture, 0);
	}
	
	public void attachDepthBuffer(int buffer) {
		GL30.glFramebufferRenderbuffer(type, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, buffer);
	}
	
	public void unbind() {
		GL30.glBindFramebuffer(type, 0);
	}
	
	public boolean isComplete() {
		return GL30.glCheckFramebufferStatus(type) == GL30.GL_FRAMEBUFFER_COMPLETE;
	}
	
	public void delete() {
		GL30.glDeleteFramebuffers(id);
	}
	
	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	public boolean equals(Object object) {
		return object instanceof GLFramebuffer && ((GLFramebuffer) object).id == id;
	}

}

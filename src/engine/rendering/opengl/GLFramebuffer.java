package engine.rendering.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import engine.rendering.Framebuffer;
import engine.rendering.Texture;

public class GLFramebuffer implements Framebuffer {
	
	private library.opengl.GLFramebuffer framebuffer;
	
	private GLTexture[] colorTextures;
	
	private GLTexture depthTexture;
	
	private int width, height, contextWidth, contextHeight;
	
	public GLFramebuffer(int width, int height, int colorAttachments, int contextWidth, int contextHeight, int type) {
		framebuffer = new library.opengl.GLFramebuffer(GL30.GL_FRAMEBUFFER);
		framebuffer.bind();
		colorTextures = new GLTexture[colorAttachments];
		for (int i = 0; i < colorAttachments; i++) {
			if (type == GL11.GL_FLOAT) {
				colorTextures[i] = new GLTexture(false, width, height, GL30.GL_RGBA16F, GL11.GL_RGBA, type);
			} else {
				colorTextures[i] = new GLTexture(false, width, height, GL11.GL_RGBA, GL11.GL_RGBA, type);
			}
			framebuffer.attachColorTexture(i, colorTextures[i].getID());
		}
		depthTexture = new GLTexture(false, width, height, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT);
		framebuffer.attachDepthTexture(depthTexture.getID());
		framebuffer.unbind();
		this.width = width;
		this.height = height;
		this.contextWidth = contextWidth;
		this.contextHeight = contextHeight;
	}

	@Override
	public void bind() {
		framebuffer.bind(width, height);
		framebuffer.drawBuffers(colorTextures.length);
	}

	@Override
	public void unbind() {
		framebuffer.unbind();
		GL11.glViewport(0, 0, contextWidth, contextHeight);		
	}

	@Override
	public Texture getColorTexture(int attachment) {
		return colorTextures[attachment];
	}

	@Override
	public Texture getDepthTexture() {
		return depthTexture;
	}

	@Override
	public void delete() {
		framebuffer.delete();
		for (int i = 0; i < colorTextures.length; i++) {
			colorTextures[i].delete();
		}
		depthTexture.delete();
	}

}

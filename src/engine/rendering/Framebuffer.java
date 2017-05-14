package engine.rendering;

public interface Framebuffer {
	
	public void bind();
	
	public void unbind();
	
	public Texture getColorTexture(int attachment);
	
	public Texture getDepthTexture();
	
	public void delete();

}

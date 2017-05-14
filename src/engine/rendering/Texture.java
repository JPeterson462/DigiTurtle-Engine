package engine.rendering;

public interface Texture {
	
	public static final int TYPE_RGB = (1 << 1), TYPE_ALPHA = (1 << 0), TYPE_RGBA = TYPE_RGB | TYPE_ALPHA;
	
	public int getWidth();
	
	public int getHeight();
	
	public int getType();
	
	public void delete();
	
	public void bind();
	
	public void bind(int unit);
	
	public void unbind();
	
	public void activeTexture(int unit);
	
	public int getID();

}

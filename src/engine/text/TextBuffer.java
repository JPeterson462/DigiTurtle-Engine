package engine.text;

import org.joml.Vector2f;

import library.font.Font;

public interface TextBuffer {
	
	public void setFont(Font font);
	
	public void setEffect(TextEffect effect);
	
	public void setPosition(Vector2f position);
	
	public void setLineWidth(float lineWidth);
	
	public void setText(String text);
	
	public void render();
	
	public void delete();

}

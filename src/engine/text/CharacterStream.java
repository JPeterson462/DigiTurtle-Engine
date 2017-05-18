package engine.text;

import java.util.ArrayList;

import org.joml.Vector4f;

import library.font.Font;

public class CharacterStream {
	
	private int size;
	
	private ArrayList<Vector4f> offsets;
	
	private ArrayList<Vector4f> textureCoords;
	
	public CharacterStream(String text, float lineWidth, Font font) {
		size = 0;
		offsets = new ArrayList<>();
		textureCoords = new ArrayList<>();
		
	}
	
	public int size() {
		return size;
	}
	
	public Vector4f getOffsetAndSize(int index) {
		return offsets.get(index);
	}
	
	public Vector4f getTextureOffsetAndSize(int index) {
		return textureCoords.get(index);
	}

}

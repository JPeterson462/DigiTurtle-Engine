package library.font;

import java.util.HashMap;

import library.opengl.GLTexture;

public class Page {
	
	private GLTexture texture;
	
	private HashMap<Character, Letter> letters;

	public Page(GLTexture texture, HashMap<Character, Letter> letters) {
		this.texture = texture;
		this.letters = letters;
	}

	public GLTexture getTexture() {
		return texture;
	}
	
	public Letter getLetter(char character) {
		return letters.get(character);
	}

	public HashMap<Character, Letter> getLetters() {
		return letters;
	}

}

package library.font;

import org.joml.Vector2f;

public class Letter {
	
	private char character;
	
	private Vector2f advance, offset, size, texCoord0, texCoord1;

	public Letter(char character, Vector2f advance, Vector2f offset, Vector2f texCoord0, Vector2f texCoord1, Vector2f size) {
		this.character = character;
		this.advance = advance;
		this.offset = offset;
		this.size = size;
		this.texCoord0 = texCoord0;
		this.texCoord1 = texCoord1;
	}

	public char getCharacter() {
		return character;
	}

	public Vector2f getAdvance() {
		return advance;
	}

	public Vector2f getOffset() {
		return offset;
	}

	public Vector2f getSize() {
		return size;
	}

	public Vector2f getTexCoord0() {
		return texCoord0;
	}

	public Vector2f getTexCoord1() {
		return texCoord1;
	}
	
	public String toString() {
		return "Letter[" + character + ", " + advance + ", " + offset + ", " + size + ", " + texCoord0 + ", " + texCoord1 + "]";
	}

}

package library.font;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Font {
	
	private Page[] pages;
	
	private String fontFace;
	
	private int maxSize;
	
	private boolean bold, italics;
	
	private Vector4f padding;
	
	private Vector2f spacing;

	private float spaceWidth, lineHeight, baseline, scaleFactor;

	public Font(Page[] pages, String fontFace, int maxSize, boolean bold, boolean italics, Vector4f padding,
			Vector2f spacing, float lineHeight, float baseline) {
		this.pages = pages;
		this.fontFace = fontFace;
		this.maxSize = maxSize;
		this.bold = bold;
		this.italics = italics;
		this.padding = padding;
		this.spacing = spacing;
		spaceWidth = calculateSpaceWidth();
		this.lineHeight = lineHeight;
		this.baseline = baseline;
	}

	private float calculateSpaceWidth() {
		float width = 0;
		for (int i = 0; i < pages.length; i++) {
			Page page = pages[i];
			for (Letter letter : page.getLetters().values()) {
				if (letter != null) {
					width = Math.max(width, letter.getAdvance().x);
				}
			}
		}
		return width;
	}
	
	public int getPage(char character) {
		for (int i = 0; i < pages.length; i++) {
			if (pages[i].getLetters().containsKey(character)) {
				return i;
			}
		}
		return -1;
	}

	public Page[] getPages() {
		return pages;
	}

	public String getFontFace() {
		return fontFace;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isItalics() {
		return italics;
	}

	public Vector4f getPadding() {
		return padding;
	}

	public Vector2f getSpacing() {
		return spacing;
	}

	public float getSpaceWidth() {
		return spaceWidth;
	}

	public float getLineHeight() {
		return lineHeight;
	}

	public float getBaseline() {
		return baseline;
	}
	
	public float getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public boolean equals(Object object) {
		if (!(object instanceof Font)) {
			return false;
		}
		Font font = (Font) object;
		return font.fontFace.equalsIgnoreCase(fontFace) && font.maxSize == maxSize;
	}
	
}

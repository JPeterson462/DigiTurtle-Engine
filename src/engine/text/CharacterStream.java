package engine.text;

import java.util.ArrayList;

import org.joml.Vector4f;

import library.font.Font;
import library.font.Letter;
import library.font.Page;
import utils.ListUtils;

public class CharacterStream {
	
	public static final int TAB_SIZE = 50;
	
	public static final int KERNING = 2;
	
	private int size;
	
	private ArrayList<Vector4f> offsets;
	
	private ArrayList<Vector4f> textureCoords;
	
	public CharacterStream(String text, float boxWidth, float boxHeight, Font font, int align) {
		size = 0;
		offsets = new ArrayList<>();
		textureCoords = new ArrayList<>();
		String[] forcedLines = text.split("\n");
		ArrayList<Line> lines = computeLines(forcedLines, font, boxWidth);
		float regionHeight = 0;
		for (int i = 0; i < lines.size(); i++) {
			regionHeight += lines.get(i).height;
		}
		float y = computeLineY(boxHeight, regionHeight, align);
		for (int i = 0; i < lines.size(); i++) {
			Line line = lines.get(i);
			float x = computeLineX(boxWidth, line.width, align);
			for (int j = 0; j < line.words.length; j++) {
				for (char c : line.words[j].toCharArray()) {
					Page page = font.getPages()[font.getPage(c)];
					Letter letter = page.getLetter(c);
					offsets.add(new Vector4f(x + letter.getOffset().x, y + letter.getOffset().y, 
							letter.getSize().x, letter.getSize().y));
					textureCoords.add(new Vector4f(letter.getTexCoord0(), letter.getTexCoord1().x, letter.getTexCoord1().y));
					x += letter.getAdvance().x + KERNING;
					size++;
				}
				x += line.gapsBetweenWords[j];
			}
			y += font.getLineHeight();
		}
	}
	
	private float computeLineY(float maxRegionHeight, float regionHeight, int align) {
		float delta = maxRegionHeight - regionHeight;
		if ((align & TextAlign.ALIGN_MIDDLE) != 0) {
			return delta / 2f;
		}
		if ((align & TextAlign.ALIGN_BOTTOM) != 0) {
			return delta;
		}
		if ((align & TextAlign.ALIGN_TOP) != 0) {
			return 0;
		}
		throw new IllegalArgumentException("Invalid align specified!");
	}
	
	private float computeLineX(float maxLineWidth, float lineWidth, int align) {
		float delta = maxLineWidth - lineWidth;
		if ((align & TextAlign.ALIGN_CENTER) != 0) {
			return delta / 2f;
		}
		if ((align & TextAlign.ALIGN_RIGHT) != 0) {
			return delta;
		}
		if ((align & TextAlign.ALIGN_LEFT) != 0) {
			return 0;
		}
		throw new IllegalArgumentException("Invalid align specified!");
	}
	
	private String wordSplitRegex = " |\t";
	
	private ArrayList<Line> computeLines(String[] forcedLines, Font font, float lineWidth) {
		ArrayList<Line> lines = new ArrayList<>();
		for (int i = 0; i < forcedLines.length; i++) {
			String[] words = forcedLines[i].split(wordSplitRegex);
			float width = 0, height = 0;
			ArrayList<String> wordList = new ArrayList<>();
			ArrayList<Float> gapsBetweenWordsList = new ArrayList<>();
			ArrayList<Boolean> tabs = findTabs(forcedLines[i]);
			for (int j = 0; j < words.length; j++) {
				String word = words[j];
				float wordWidth = getWordWidth(word, font);
				float wordHeight = getWordHeight(word, font);
				if (width + font.getSpaceWidth() + wordWidth > lineWidth && width > 0) {
					Line line = new Line();
					line.gapsBetweenWords = ListUtils.toIntegerArray(gapsBetweenWordsList);
					line.height = height;
					line.width = width;
					line.words = wordList.toArray(new String[0]);
					lines.add(line);
					gapsBetweenWordsList.clear();
					height = 0;
					width = -font.getSpaceWidth();
					wordList.clear();
				}
				height = Math.max(height, wordHeight);
				float gapWidth = computeGapWidth(width, tabs.get(j), font);
				width += gapWidth + wordWidth;
				wordList.add(word);
				gapsBetweenWordsList.add(gapWidth);
			}
			if (wordList.size() > 0) {
				Line line = new Line();
				line.gapsBetweenWords = ListUtils.toIntegerArray(gapsBetweenWordsList);
				line.height = height;
				line.width = width;
				line.words = wordList.toArray(new String[0]);
				lines.add(line);
			}
		}
		return lines;
	}
	
	private float computeGapWidth(float width, boolean tab, Font font) {
		int count0 = (int) Math.floorDiv((int) width, TAB_SIZE);
		float next = (TAB_SIZE) * (count0 + 1);
		return tab ? Math.max(font.getSpaceWidth(), next - width) : font.getSpaceWidth();
	}
	
	private ArrayList<Boolean> findTabs(String line) {
		ArrayList<Boolean> tabs = new ArrayList<>();
		tabs.add(false);
		for (char c : line.toCharArray()) {
			if (c == ' ') {
				tabs.add(false);
			}
			else if (c == '\t') {
				tabs.add(true);
			}
		}
		return tabs;
	}
	
	private float getWordHeight(String word, Font font) {
		float height = 0;
		for (char c : word.toCharArray()) {
			height = Math.max(height, font.getPages()[font.getPage(c)].getLetter(c).getSize().y);
		}
		return height;
	}
	
	private float getWordWidth(String word, Font font) {
		float width = 0;
		for (char c : word.toCharArray()) {
			width += font.getPages()[font.getPage(c)].getLetter(c).getAdvance().x;
		}
		return width;
	}
	
	class Line {
		String[] words;
		float width, height;
		float[] gapsBetweenWords;
		boolean[] tabs;
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

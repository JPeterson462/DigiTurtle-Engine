package library.font;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Angelcode {

	public static class Info {
		
		public final String face;
		
		public final int size;
		
		public final boolean bold, italic;
		
		public final String charset;
		
		public final boolean unicode;
		
		public final int stretchH;
		
		public final boolean smooth;
		
		public final boolean antiAliased;
		
		public final Vector4f padding;
		
		public final Vector2f spacing;

		public Info(String face, int size, boolean bold, boolean italic, String charset, 
				boolean unicode, int stretchH, boolean smooth, boolean antiAliased, 
				Vector4f padding, Vector2f spacing) {
			this.face = face;
			this.size = size;
			this.bold = bold;
			this.italic = italic;
			this.charset = charset;
			this.unicode = unicode;
			this.stretchH = stretchH;
			this.smooth = smooth;
			this.antiAliased = antiAliased;
			this.padding = padding;
			this.spacing = spacing;
		}
		
	}
	
	public static class FontData {
		
		public final int lineHeight, baseline, scaleWidth, scaleHeight, pages;
		
		public final boolean packed;

		public FontData(int lineHeight, int baseline, int scaleWidth, int scaleHeight, int pages, boolean packed) {
			this.lineHeight = lineHeight;
			this.baseline = baseline;
			this.scaleWidth = scaleWidth;
			this.scaleHeight = scaleHeight;
			this.pages = pages;
			this.packed = packed;
		}
		
	}
	
	public static class Page {
		
		public final int id;
		
		public final String file;
		
		public Page(int id, String file) {
			this.id = id;
			this.file = file;
		}
		
	}

	public static class Letter {
		
		public final char c;
		
		public final int x, y, width, height;
		
		public final int xOffset, yOffset;
		
		public final int xAdvance;
		
		public final int page;
		
		public final int channel;

		public Letter(char c, int x, int y, int width, int height, int xOffset, 
				int yOffset, int xAdvance, int page, int channel) {
			this.c = c;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.xAdvance = xAdvance;
			this.page = page;
			this.channel = channel;
		}
		
	}
	
}

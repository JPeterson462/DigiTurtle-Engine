package library.font;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import library.opengl.GLTexture;
import utils.IOUtils;
import utils.RelativeStreamGenerator;

public class TruetypeFontImporter implements FontImporter {

	private String[] EXTENSIONS = { "ttf" };

	private final STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();
	
	private final FloatBuffer xb = MemoryUtil.memAllocFloat(1), 
								yb = MemoryUtil.memAllocFloat(1);
	
	@Override
	public String[] getExtensions() {
		return EXTENSIONS;
	}

	@Override
	public Font importFont(InputStream stream, float fontSize, RelativeStreamGenerator generator) {
		final int fontPageSize = 512;
		final int characterRangeLength = 95, characterRangeOffset = 32;
		xb.put(0, 0);
		yb.put(0, 0);
		GLTexture texture = new GLTexture(GL11.GL_TEXTURE_2D);
		STBTTPackedchar.Buffer chardata = STBTTPackedchar.malloc(128);
		Font font;
		try (STBTTPackContext packContext = STBTTPackContext.malloc()) {
			ByteBuffer ttf = IOUtils.readBufferQuietly(stream);
			ByteBuffer bitmap = BufferUtils.createByteBuffer(fontPageSize * fontPageSize);
			STBTruetype.stbtt_PackBegin(packContext, bitmap, fontPageSize, fontPageSize, 0, 1, MemoryUtil.NULL);
			int p = characterRangeOffset;
			chardata.position(p);
			chardata.limit(p + characterRangeLength);
			STBTruetype.stbtt_PackSetOversampling(packContext, 1, 1);
			STBTruetype.stbtt_PackFontRange(packContext, ttf, 0, fontSize, characterRangeOffset, chardata);
			chardata.clear();
			STBTruetype.stbtt_PackEnd(packContext);
			texture.bind();
			texture.texImage(GL11.GL_ALPHA, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, bitmap, fontPageSize, fontPageSize);
			texture.magFilter(GL11.GL_LINEAR);
			texture.minFilter(GL11.GL_LINEAR);
			texture.unbind();
			HashMap<Character, Letter> letters = new HashMap<>();
			for (int cOffset = 0; cOffset < characterRangeOffset; cOffset++) {
				char c = (char) (characterRangeOffset + cOffset);
				STBTruetype.stbtt_GetPackedQuad(chardata, fontPageSize, fontPageSize, c, xb, yb, quad, true);
				letters.put(c, new Letter(c, new Vector2f(xb.get(0), yb.get(0)), new Vector2f(quad.x0(), quad.y0()), 
						new Vector2f(quad.s0(), quad.t0()), new Vector2f(quad.s1(), quad.t1()), new Vector2f(quad.x1() - quad.x0(), quad.y1() - quad.y0())));
			}
			Page page = new Page(texture, letters);
			font = new Font(new Page[] { page }, "font_face", (int) fontSize, false, false, new Vector4f(0), null, fontSize, fontSize);
		}
		return font;
	}

}

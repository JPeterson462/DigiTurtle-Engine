package engine.rendering.opengl;

import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;

import engine.rendering.Texture;
import utils.IOUtils;

public class GLTexture implements Texture {
	
	private int width, height, type;
	
	private library.opengl.GLTexture texture;
	
	public GLTexture(InputStream right, InputStream left, InputStream top, InputStream bottom, InputStream back, InputStream front) {
		int[] widthBuf = {0}, heightBuf = {0}, componentsBuf = {0};
		ByteBuffer rightData = IOUtils.readBufferQuietly(right);
		ByteBuffer leftData = IOUtils.readBufferQuietly(left);
		ByteBuffer topData = IOUtils.readBufferQuietly(top);
		ByteBuffer bottomData = IOUtils.readBufferQuietly(bottom);
		ByteBuffer backData = IOUtils.readBufferQuietly(back);
		ByteBuffer frontData = IOUtils.readBufferQuietly(front);
		ByteBuffer rightBuf = STBImage.stbi_load_from_memory(rightData, widthBuf, heightBuf, componentsBuf, 0);
		int rightComponents = componentsBuf[0];
		int rightType = getFormat(rightComponents);
		ByteBuffer leftBuf = STBImage.stbi_load_from_memory(leftData, widthBuf, heightBuf, componentsBuf, 0);
		int leftComponents = componentsBuf[0];
		int leftType = getFormat(leftComponents);
		ByteBuffer topBuf = STBImage.stbi_load_from_memory(topData, widthBuf, heightBuf, componentsBuf, 0);
		int topComponents = componentsBuf[0];
		int topType = getFormat(topComponents);
		ByteBuffer bottomBuf = STBImage.stbi_load_from_memory(bottomData, widthBuf, heightBuf, componentsBuf, 0);
		int bottomComponents = componentsBuf[0];
		int bottomType = getFormat(bottomComponents);
		ByteBuffer backBuf = STBImage.stbi_load_from_memory(backData, widthBuf, heightBuf, componentsBuf, 0);
		int backComponents = componentsBuf[0];
		int backType = getFormat(backComponents);
		ByteBuffer frontBuf = STBImage.stbi_load_from_memory(frontData, widthBuf, heightBuf, componentsBuf, 0);
		int frontComponents = componentsBuf[0];
		int frontType = getFormat(frontComponents);
		width = widthBuf[0];
		height = heightBuf[0];
		type = getType(Math.min(Math.min(rightComponents, Math.min(leftComponents, topComponents)), Math.min(bottomComponents, Math.min(backComponents, frontComponents))));
		int format = getFormat(Math.min(Math.min(rightComponents, Math.min(leftComponents, topComponents)), Math.min(bottomComponents, Math.min(backComponents, frontComponents))));
		if (type < 0) {
			throw new IllegalArgumentException("Invalid texture format supplied");
		}
		texture = new library.opengl.GLTexture(GL13.GL_TEXTURE_CUBE_MAP);
		texture.bind();
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, format, width, height, 0, rightType, GL11.GL_UNSIGNED_BYTE, rightBuf);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, format, width, height, 0, leftType, GL11.GL_UNSIGNED_BYTE, leftBuf);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, format, width, height, 0, topType, GL11.GL_UNSIGNED_BYTE, topBuf);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, format, width, height, 0, bottomType, GL11.GL_UNSIGNED_BYTE, bottomBuf);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, format, width, height, 0, backType, GL11.GL_UNSIGNED_BYTE, backBuf);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, format, width, height, 0, frontType, GL11.GL_UNSIGNED_BYTE, frontBuf);
		texture.magFilter(GL11.GL_LINEAR);
		texture.minFilter(GL11.GL_LINEAR);
		texture.wrapS(GL12.GL_CLAMP_TO_EDGE);
		texture.wrapT(GL12.GL_CLAMP_TO_EDGE);
		texture.unbind();
	}
	
	public GLTexture(InputStream stream, boolean repeat, boolean anisotropicFiltering) {
		ByteBuffer data = IOUtils.readBufferQuietly(stream);
		int[] widthBuf = {0}, heightBuf = {0}, componentsBuf = {0};
		ByteBuffer pixels = STBImage.stbi_load_from_memory(data, widthBuf, heightBuf, componentsBuf, 0);
		width = widthBuf[0];
		height = heightBuf[0];
		type = getType(componentsBuf[0]);
		if (type < 0) {
			throw new IllegalArgumentException("Invalid texture format supplied: " + stream);
		}
		texture = new library.opengl.GLTexture(GL11.GL_TEXTURE_2D);
		texture.bind();
		texture.magFilter(GL11.GL_LINEAR);
		texture.minFilter(GL11.GL_LINEAR_MIPMAP_LINEAR);
		if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			texture.useAnisotropicFiltering();
		}
		int wrap = repeat ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE;
		texture.wrapS(wrap);
		texture.wrapT(wrap);
		int format = getFormat(componentsBuf[0]);
		texture.texImage(format, format, GL11.GL_UNSIGNED_BYTE, pixels, width, height);
		texture.generateMipmaps();
		texture.unbind();
	}
	
	public GLTexture(boolean repeat, int width, int height, int format, int type) {
		this.width = width;
		this.height = height;
		this.type = -1;
		texture = new library.opengl.GLTexture(GL11.GL_TEXTURE_2D);
		texture.bind();
		texture.magFilter(GL11.GL_LINEAR);
		texture.minFilter(GL11.GL_LINEAR_MIPMAP_LINEAR);
		int wrap = repeat ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE;
		texture.wrapS(wrap);
		texture.wrapT(wrap);
		texture.texImage(format, format, type, null, width, height);
		texture.generateMipmaps();
		texture.unbind();
	}
	
	private int getFormat(int components) {
		if (components == 4) {
			return GL11.GL_RGBA;
		}
		if (components == 3) {
			return GL11.GL_RGB;
		}
		if (components == 1) {
			return GL11.GL_ALPHA;
		}
		return -1;
	}
	
	private int getType(int components) {
		if (components == 4) {
			return TYPE_RGBA;
		}
		if (components == 3) {
			return TYPE_RGB;
		}
		if (components == 1) {
			return TYPE_ALPHA;
		}
		return -1;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public int getType() {
		return type;
	}

	@Override
	public void delete() {
		texture.delete();
	}

	@Override
	public void bind() {
		texture.bind();
	}

	@Override
	public void bind(int unit) {
		texture.activeTexture(unit);
		texture.bind();
	}

	@Override
	public void unbind() {
		texture.unbind();
	}

	@Override
	public void activeTexture(int unit) {
		texture.activeTexture(unit);
	}

	@Override
	public int getID() {
		return texture.getID();
	}
	
	public int hashCode() {
		return texture.hashCode();
	}

}

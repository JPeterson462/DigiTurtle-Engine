package utils;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImageWrite;

public class Screenshot {
	
	public static void takeScreenshot(String filename, int width, int height) {
		takeScreenshot(filename, 0, 0, width, height);
	}
	
	public static void takeScreenshot(String filename, int x, int y, int width, int height) {
		GL11.glReadBuffer(GL11.GL_FRONT);
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		GL11.glReadPixels(x, y, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		STBImageWrite.stbi_write_png(filename, width, height, 4, buffer, width * 4);
	}

}

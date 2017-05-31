package utils;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;

public class MathUtils {
	
	public static void putAbsolute(Matrix4f matrix, FloatBuffer buffer) {
		buffer.put(matrix.m00());
		buffer.put(matrix.m01());
		buffer.put(matrix.m02());
		buffer.put(matrix.m03());
		buffer.put(matrix.m10());
		buffer.put(matrix.m11());
		buffer.put(matrix.m12());
		buffer.put(matrix.m13());
		buffer.put(matrix.m20());
		buffer.put(matrix.m21());
		buffer.put(matrix.m22());
		buffer.put(matrix.m23());
		buffer.put(matrix.m30());
		buffer.put(matrix.m31());
		buffer.put(matrix.m32());
		buffer.put(matrix.m33());
	}
	
	public static String toString(FloatBuffer buffer, int max, int newline) {
		String string = "";
		for (int i = 0; i < buffer.limit() && i < max; i++) {
			string += buffer.get(i) + " ";
			if ((i + 1) % newline == 0 && i > 0) {
				string += "\n";
			}
		}
		return string.trim();
	}

}

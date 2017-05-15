package library.models.collada;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ColladaUtils {

	private static final Matrix4f CORRECTION = new Matrix4f().rotateX((float) Math.toRadians(-90));

	public static void correct(Vector4f vector) {
		CORRECTION.transform(vector);
	}

	public static void correct(Matrix4f matrix) {
		CORRECTION.mul(matrix, matrix);
	}

}

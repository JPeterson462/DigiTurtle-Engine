package utils;

import java.util.ArrayList;

import engine.rendering.Vertex;

public class CubeGenerator {

	public ArrayList<Vertex> generateCube(final float size) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		for (int i = 0; i < VERTICES.length; i += 3) {
			vertices.add(new Vertex().position(VERTICES[i] * size, VERTICES[i + 1] * size, VERTICES[i + 2] * size));
		}
		return vertices;
	}

	private final float SIZE = 1;
	
	private float[] VERTICES = { //
			-SIZE, SIZE, -SIZE, //
			-SIZE, -SIZE, -SIZE, //
			SIZE, -SIZE, -SIZE, //
			SIZE, -SIZE, -SIZE, //
			SIZE, SIZE, -SIZE, //
			-SIZE, SIZE, -SIZE, //

			-SIZE, -SIZE, SIZE, //
			-SIZE, -SIZE, -SIZE, //
			-SIZE, SIZE, -SIZE, //
			-SIZE, SIZE, -SIZE, //
			-SIZE, SIZE, SIZE, //
			-SIZE, -SIZE, SIZE, //

			SIZE, -SIZE, -SIZE, //
			SIZE, -SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, SIZE, -SIZE, //
			SIZE, -SIZE, -SIZE, //

			-SIZE, -SIZE, SIZE, //
			-SIZE, SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, -SIZE, SIZE, //
			-SIZE, -SIZE, SIZE, //

			-SIZE, SIZE, -SIZE, //
			SIZE, SIZE, -SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			-SIZE, SIZE, SIZE, //
			-SIZE, SIZE, -SIZE, //

			-SIZE, -SIZE, -SIZE, //
			-SIZE, -SIZE, SIZE, //
			SIZE, -SIZE, -SIZE, //
			SIZE, -SIZE, -SIZE, //
			-SIZE, -SIZE, SIZE, //
			SIZE, -SIZE, SIZE //
	};

}

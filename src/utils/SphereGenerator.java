package utils;

import java.util.ArrayList;

import org.joml.Vector3f;

import engine.rendering.Vertex;

public class SphereGenerator {

	public static void generateSphere(ArrayList<Vertex> vertices, ArrayList<Integer> indices) {
		float delta = 10;
		float radius = 1;
		int index = 0;
		for (float theta = 0; theta < 360; theta += delta) {
			for (float phi = 0; phi < 360; phi += delta, index += 4) {
				vertices.add(getSphericalVertex(radius, theta, phi));
				vertices.add(getSphericalVertex(radius, theta + delta, phi));
				vertices.add(getSphericalVertex(radius, theta + delta, phi + delta));
				vertices.add(getSphericalVertex(radius, theta, phi + delta));
				indices.add(index + 0);
				indices.add(index + 1);
				indices.add(index + 2);
				indices.add(index + 2);
				indices.add(index + 3);
				indices.add(index + 0);
			}
		}
	}

	private static Vertex getSphericalVertex(float radius, float theta, float phi) {
		Vector3f position = getSpherical(radius, theta, phi);
		return new Vertex().position(position);
	}

	private static Vector3f getSpherical(float radius, float theta, float phi) {
		double x = radius * Math.sin(Math.toRadians(phi)) * Math.cos(Math.toRadians(theta));
		double y = radius * Math.cos(Math.toRadians(phi));
		double z = radius * Math.sin(Math.toRadians(phi)) * Math.sin(Math.toRadians(theta));
		return new Vector3f((float) x, (float) y, (float) z);
	}

}

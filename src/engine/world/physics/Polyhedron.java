package engine.world.physics;

import java.util.ArrayList;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Polyhedron {

	private ArrayList<Vector3f> vertices = new ArrayList<>();
	
	private ArrayList<Vector3f> projectedVertices = new ArrayList<>();
	
	private Vector3f position = new Vector3f();
	
	public Vector3f getPosition() {
		return position;
	}
	
	public ArrayList<Vector3f> getVertices() {
		return projectedVertices;
	}
	
	public void clear() {
		vertices.clear();
	}
	
	public Polyhedron addVertex(float x, float y, float z) {
		vertices.add(new Vector3f(x, y, z));
		return this;
	}
	
	public Polyhedron addVertex(Vector3f vertex) {
		vertices.add(vertex);
		return this;
	}
	
	public void project(Quaternionf orientation, Vector3f position) {
		this.position.set(position);
		projectedVertices.clear();
		for (int i = 0; i < vertices.size(); i++) {
			Vector3f projectedVertex = new Vector3f();
			projectedVertex.set(vertices.get(i));
			projectedVertex.rotate(orientation);
			projectedVertex.add(position);
			projectedVertices.add(projectedVertex);
		}
	}
	
}

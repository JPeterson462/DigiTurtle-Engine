package engine.world.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PolyhedronBuilder {
	
	private Vector3f zeroVector = new Vector3f().zero();
	
	private Quaternionf unitQuaternion = new Quaternionf().identity();
	
	private Vector3f tmpVector = new Vector3f();
	public void addVertex(Polyhedron polyhedron, Vector3f offset, Quaternionf orientation, float halfWidth, float halfHeight, float halfThickness) {
		tmpVector.set(halfWidth, halfHeight, halfThickness).rotate(orientation).add(offset);
		polyhedron.addVertex(new Vector3f(tmpVector));
	}
	
	public Polyhedron buildCube(float sideLength) {
		return buildCube(zeroVector, unitQuaternion, sideLength);
	}

	public Polyhedron buildCube(Vector3f offset, Quaternionf orientation, float sideLength) {
		return buildCube(offset, orientation, sideLength, sideLength, sideLength);
	}
	
	public Polyhedron buildCube(float width, float height, float depth) {
		return buildCube(zeroVector, unitQuaternion, width, height, depth);
	}

	public Polyhedron buildCube(Vector3f offset, Quaternionf orientation, float width, float height, float depth) {
		float halfWidth = width * 0.5f, halfHeight = height * 0.5f, halfThickness = depth * 0.5f;
		Polyhedron polyhedron = new Polyhedron();
		// Front face
		addVertex(polyhedron, offset, orientation, -halfWidth, -halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, -halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, -halfWidth, +halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, +halfHeight, +halfThickness);
		// Right face
		addVertex(polyhedron, offset, orientation, +halfWidth, +halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, -halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, +halfHeight, -halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, -halfHeight, -halfThickness);
		// Back face
		addVertex(polyhedron, offset, orientation, +halfWidth, -halfHeight, -halfThickness);
		addVertex(polyhedron, offset, orientation, -halfWidth, -halfHeight, -halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, +halfHeight, -halfThickness);
		addVertex(polyhedron, offset, orientation, -halfWidth, +halfHeight, -halfThickness);
		// Left face
		addVertex(polyhedron, offset, orientation, -halfWidth, +halfHeight, -halfThickness);
		addVertex(polyhedron, offset, orientation, -halfWidth, -halfHeight, -halfThickness);
		addVertex(polyhedron, offset, orientation, -halfWidth, +halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, -halfWidth, -halfHeight, +halfThickness);
		// Bottom face
		addVertex(polyhedron, offset, orientation, -halfWidth, -halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, -halfWidth, -halfHeight, -halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, -halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, -halfHeight, -halfThickness);
		// Move to top
		addVertex(polyhedron, offset, orientation, +halfWidth, -halfHeight, -halfThickness);
		addVertex(polyhedron, offset, orientation, -halfWidth, +halfHeight, +halfThickness);
		// Top face
		addVertex(polyhedron, offset, orientation, -halfWidth, +halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, +halfHeight, +halfThickness);
		addVertex(polyhedron, offset, orientation, -halfWidth, +halfHeight, -halfThickness);
		addVertex(polyhedron, offset, orientation, +halfWidth, +halfHeight, -halfThickness);
		return polyhedron;
	}

}

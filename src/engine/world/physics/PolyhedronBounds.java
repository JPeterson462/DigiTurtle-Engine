package engine.world.physics;

import java.util.ArrayList;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import utils.Accessor;

public class PolyhedronBounds implements Bounds {
	
	private Accessor<Vector3f> getPosition;
	
	private Accessor<Quaternionf> getOrientation;
	
	private Polyhedron polyhedron;
	
	private class Overlap {
		private Vector3f overlapV, overlapN; // minimum translation vector, overlap axis
		private float overlap; // overlap distance
		private boolean aInB, bInA;
		public Overlap() {
			aInB = true;
			bInA = true;
			overlapV = new Vector3f();
			overlapN = new Vector3f();
		}
	}
	
	public PolyhedronBounds(Polyhedron polyhedron) {
		this.polyhedron = polyhedron;
	}

	@Override
	public void setAccessors(Accessor<Vector3f> getPosition, Accessor<Quaternionf> getOrientation) {
		this.getPosition = getPosition;
		this.getOrientation = getOrientation;
	}

	@Override
	public boolean overlaps(Bounds other) {
		return overlaps(other, new Overlap());
	}
	
	@Override
	public Vector3f getOverlap(Bounds other) {
		Overlap overlap = new Overlap();
		if (overlaps(other, overlap)) {
			overlap.overlapV.set(overlap.overlapN).mul(overlap.overlap);
			return overlap.overlapV;
		}
		return null;
	}
	
	private boolean overlaps(Bounds other, Overlap overlap) {
		polyhedron.project(getOrientation.get(), getPosition.get());
		if (other instanceof PolyhedronBounds) {
			PolyhedronBounds polyOther = (PolyhedronBounds) other;
			polyOther.polyhedron.project(polyOther.getOrientation.get(), polyOther.getPosition.get());
			ArrayList<Vector3f> a = polyhedron.getVertices();
			ArrayList<Vector3f> b = polyOther.polyhedron.getVertices();
			Vector3f axis = new Vector3f(), edge1 = new Vector3f(), edge2 = new Vector3f();
			Vector3f v1, v2, v3;
			for (int v = 0; v < a.size() - 2; v++) {
				if ((v & 1) != 0) {
					v1 = a.get(v + 0);
					v2 = a.get(v + 1);
					v3 = a.get(v + 2);
				} else {
					v1 = a.get(v + 0);
					v2 = a.get(v + 2);
					v3 = a.get(v + 1);
				}
				edge1.set(v2).sub(v1);
				edge2.set(v3).sub(v1);
				axis.set(edge1).cross(edge2).normalize();
				if (axis.lengthSquared() == 0) {
					continue;
				}
				if (isSeparatingAxis(a, polyhedron.getPosition(), b, polyOther.polyhedron.getPosition(), axis, overlap)) {
					return false;
				}
			}
			for (int v = 0; v < b.size() - 2; v++) {
				if ((v & 1) != 0) {
					v1 = b.get(v + 0);
					v2 = b.get(v + 1);
					v3 = b.get(v + 2);
				} else {
					v1 = b.get(v + 0);
					v2 = b.get(v + 2);
					v3 = b.get(v + 1);
				}
				edge1.set(v2).sub(v1);
				edge2.set(v3).sub(v1);
				axis.set(edge1).cross(edge2).normalize();
				if (axis.lengthSquared() == 0) {
					continue;
				}
				if (isSeparatingAxis(a, polyhedron.getPosition(), b, polyOther.polyhedron.getPosition(), axis, overlap)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean isSeparatingAxis(ArrayList<Vector3f> a, Vector3f aPosition, ArrayList<Vector3f> b, Vector3f bPosition, Vector3f axis, Overlap overlap) {
		Vector3f offset = new Vector3f();
		Vector2f rangeA = new Vector2f(), rangeB = new Vector2f();
		offset.set(bPosition).sub(aPosition);
		float projectedOffset = offset.dot(axis);
		flattenPoints(a, axis, rangeA);
		flattenPoints(b, axis, rangeB);
		rangeB.add(projectedOffset, projectedOffset);
		if (rangeA.x > rangeB.y || rangeB.x > rangeA.y) {
			return true;
		}
		float overlapDistance;
		if (rangeA.x < rangeB.x) {
			overlap.aInB = false;
			if (rangeA.y < rangeB.y) {
				overlapDistance = rangeA.y - rangeB.x;
				overlap.bInA = false;
			} else {
				float delta1 = rangeA.y - rangeB.x;
				float delta2 = rangeB.y - rangeA.x;
				overlapDistance = delta1 < delta2 ? delta1 : -delta2;
			}
		} else {
			overlap.bInA = false;
			if (rangeA.y > rangeB.y) {
				overlapDistance = rangeA.y - rangeB.x;
				overlap.aInB = false;
			} else {
				float delta1 = rangeA.y - rangeB.x;
				float delta2 = rangeB.y - rangeA.x;
				overlapDistance = delta1 < delta2 ? delta1 : -delta2;
			}
		}
		overlapDistance = Math.abs(overlapDistance);
		overlap.overlap = overlapDistance;
		overlap.overlapN.set(axis.normalize());
		if (overlapDistance < 0) {
			overlap.overlapN.negate();
		}
		return false;
	}
	
	private void flattenPoints(ArrayList<Vector3f> vertices, Vector3f axis, Vector2f projection) {
		float min = axis.dot(vertices.get(0));
		float max = min;
		for (int i = 0; i < vertices.size(); i++) {
			float dot = axis.dot(vertices.get(i));
			if (dot < min) {
				min = dot;
			}
			if (dot > max) {
				max = dot;
			}
		}
		projection.set(min, max);
	}

}

package engine.world.physics;

import org.joml.Vector3f;

public class Triangle {
	
	private static final double EPSILON = 0.000001;
	
	private Vector3f point1, point2, point3;
	
	private Vector3f normal;
	
	private float d;
	
	public Triangle(Vector3f point1, Vector3f point2, Vector3f point3) {
		this.point1 = point1;
		this.point2 = point2;
		this.point3 = point3;
		Vector3f edge1 = new Vector3f(point2).sub(point1);
		Vector3f edge2 = new Vector3f(point3).sub(point1);
		normal = new Vector3f(edge1).cross(edge2);
		d = -point1.dot(normal);
	}
	
	public Vector3f getNormal() {
		return normal;
	}
	
	public Vector3f intersects(Ray ray) {
		if (!intersection(ray)) {
			return null;
		}
		float t = -(d + ray.getOrigin().dot(normal)) / (ray.getDirection().dot(normal));
		return new Vector3f(ray.getOrigin()).fma(t, ray.getDirection());
	}
	
	private boolean intersection(Ray ray) {
		boolean result = false;
		Vector3f edge1 = new Vector3f(point2).sub(point1);
		Vector3f edge2 = new Vector3f(point3).sub(point1);
		Vector3f point = new Vector3f(ray.getDirection()).cross(edge2);
		float determinant = edge1.dot(point);
		if (determinant > -EPSILON && determinant < EPSILON) {
			result = false;
		} else {
			float invDeterminant = 1f / determinant;
			Vector3f t1 = new Vector3f(ray.getOrigin()).sub(point1);
			float u = t1.dot(point) * invDeterminant;
			if (u < 0 || u > 1) {
				result = false;
			} else {
				Vector3f q = new Vector3f(t1).cross(edge1);
				float v = ray.getDirection().dot(q) * invDeterminant;
				if (v < 0 || v + u > 1) {
					result = false;
				} else {
					float t = edge2.dot(q) * invDeterminant;
					if (t > EPSILON) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	public Vector3f getPoint1() {
		return point1;
	}

	public Vector3f getPoint2() {
		return point2;
	}

	public Vector3f getPoint3() {
		return point3;
	}
	
	private float area(float x1, float y1, float x2, float y2, float x3, float y3) {
		return Math.abs((x1*(y2-y3) + x2*(y3-y1)+ x3*(y1-y2))/2.0f);
	}
	
	public boolean contains(float x, float z) {
		float A = area(point1.x, point1.z, point2.x, point2.x, point3.x, point3.z);
		float A1 = area(x, z, point2.x, point2.x, point3.x, point3.z);
		float A2 = area(point1.x, point1.z, x, x, point3.x, point3.z);
		float A3 = area(point1.x, point1.z, point2.x, point2.x, x, z);
		return A == (A1 + A2 + A3);
	}

}

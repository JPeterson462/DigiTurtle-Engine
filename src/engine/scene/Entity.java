package engine.scene;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import engine.rendering.Geometry;

public class Entity {
	
	public static final int NORMAL_MAPPED_BIT = (1 << 0);
	
	public static final int SKELETAL_BIT = (1 << 1);
	
	private int flags;
	
	private Geometry geometry;
	
	private Material material;
	
	private Skeleton skeleton;
	
	private float x = 0, y = 0, z = 0;
	
	private float sx = 1, sy = 1, sz = 1;
	
	private float qx = 0, qy = 0, qz = 0, qw = 1;
	
	private Vector3f position = new Vector3f(), scale = new Vector3f();
	
	private Quaternionf orientation = new Quaternionf();

	public Entity(Geometry geometry, Material material, boolean normalMapped) {
		this.geometry = geometry;
		this.material = material;
		flags = (normalMapped ? NORMAL_MAPPED_BIT : 0);
	}

	public Entity(Geometry geometry, Material material, Skeleton skeleton, boolean normalMapped) {
		this.geometry = geometry;
		this.material = material;
		this.skeleton = skeleton;
		flags = (normalMapped ? NORMAL_MAPPED_BIT : 0) | SKELETAL_BIT;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public Skeleton getSkeleton() {
		return skeleton;
	}
	
	public int getFlags() {
		return flags;
	}

	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
	}

	public Vector3f getPosition() {
		position.set(x, y, z);
		return position;
	}

	public void setPosition(Vector3f position) {
		x = position.x;
		y = position.y;
		z = position.z;
	}

	public Vector3f getScale() {
		scale.set(sx, sy, sz);
		return scale;
	}

	public void setScale(Vector3f scale) {
		sx = scale.x;
		sy = scale.y;
		sz = scale.z;
	}

	public Quaternionf getOrientation() {
		orientation.set(qx, qy, qz, qw);
		return orientation;
	}

	public void setOrientation(Quaternionf orientation) {
		qx = orientation.x;
		qy = orientation.y;
		qz = orientation.z;
		qw = orientation.w;
	}

}

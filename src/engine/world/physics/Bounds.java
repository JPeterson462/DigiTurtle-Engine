package engine.world.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import utils.Accessor;

public interface Bounds {
	
	public void setAccessors(Accessor<Vector3f> getPosition, Accessor<Quaternionf> getOrientation);

	public boolean overlaps(Bounds other);
	
	public Vector3f getOverlap(Bounds other);
	
}

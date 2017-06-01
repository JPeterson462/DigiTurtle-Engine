package engine.world.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import utils.Accessor;

public class CuboidBounds implements Bounds {
	
	private Accessor<Vector3f> getPosition;
	
	private Accessor<Quaternionf> getOrientation;

	@Override
	public void setAccessors(Accessor<Vector3f> getPosition, Accessor<Quaternionf> getOrientation) {
		this.getPosition = getPosition;
		this.getOrientation = getOrientation;
	}

	@Override
	public boolean overlaps(Bounds other) {
		if (other instanceof CuboidBounds) {
			return CuboidCuboidUtils.overlaps(this, getPosition.get(), getOrientation.get(), 
					(CuboidBounds) other, ((CuboidBounds) other).getPosition.get(), ((CuboidBounds) other).getOrientation.get());
		}
		return false;
	}

	@Override
	public Vector3f getOverlap(Bounds other) {
		return null;
	}

}

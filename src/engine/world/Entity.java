package engine.world;

import java.util.HashMap;
import java.util.Map;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Entity {
	
	private float x = 0, y = 0, z = 0;
	
	private float sx = 1, sy = 1, sz = 1;
	
	private float qx = 0, qy = 0, qz = 0, qw = 1;
	
	private Vector3f position = new Vector3f(), scale = new Vector3f();
	
	private Quaternionf orientation = new Quaternionf();
	
	private HashMap<Class<? extends Component>, Component> components = new HashMap<>();

	public void addComponent(Component component) {
		Class<? extends Component> type = component.getClass();
		if (components.containsKey(type)) {
			throw new IllegalArgumentException("Attempted to add duplicate component.");
		}
		components.put(type, component);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> type) {
		return (T) components.get(type);
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
	
	public void update(float delta) {
		for (Map.Entry<Class<? extends Component>,Component> component : components.entrySet()) {
			component.getValue().update(this, delta);
		}
	}

}

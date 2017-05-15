package engine.world;

public interface Component {

	public static final int NORMAL_MAPPED_BIT = (1 << 0);
	
	public static final int SKELETAL_BIT = (1 << 1);
	
	public void update(Entity entity, float delta);
	
}

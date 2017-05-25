package engine.world;

@FunctionalInterface
public interface TerrainGenerator {
	
	public float getHeightAt(float x, float z);

}

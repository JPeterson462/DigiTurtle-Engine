package engine.world;

import engine.rendering.Renderer;

public class Terrain {
	
	private TerrainGenerator generator; // use the generator so that instead of interpolating the heights you just do a lookup
	
	public Terrain(TerrainGenerator generator, int width, int height, float resolution) {
		
	}
	
	public void create(Renderer renderer) {
		
	}
	
	public float getHeightAt(float x, float z) {
		return generator.getHeightAt(x, z);
	}

}

package engine.scene;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3f;

import engine.Camera;
import engine.rendering.Geometry;
import engine.world.Entity;
import engine.world.Light;
import engine.world.Material;
import engine.world.Terrain;

public interface RenderingPipeline {
	
	public void doGeometryPass(Camera camera, HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultSkeletalEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedSkeletalEntities,
			Terrain terrain);
	
	public void doLightingPass(float lightLevel, Camera camera, ArrayList<Light> lights, Vector3f cameraPosition);
	
	public void doFXAAPass();
	
	public void doFinalRender();

}

package engine.scene;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3f;

import engine.Camera;
import engine.rendering.Geometry;

public interface RenderingPipeline {
	
	public void doGeometryPass(Camera camera, HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultSkeletalEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedSkeletalEntities);
	
	public void doLightingPass(float lightLevel, Camera camera, ArrayList<Light> lights, Vector3f cameraPosition);
	
	public void doFinalRender();

}

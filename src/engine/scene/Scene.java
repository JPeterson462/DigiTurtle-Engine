package engine.scene;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3f;

import engine.Camera;
import engine.rendering.Geometry;
import engine.rendering.Renderer;

public class Scene {
	
	private RenderingPipeline pipeline;
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultSkeletalEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedSkeletalEntities = new HashMap<>();
	
	private ArrayList<Light> lights = new ArrayList<>();
	
	public Scene(Renderer renderer, RenderingStrategy strategy) {
		switch (strategy) {
			case DEFERRED:
				pipeline = new DeferredRenderingPipeline(renderer);
				break;
		}
	}
	
	public void addLight(Light light) {
		lights.add(light);
	}
	
	public void addEntity(Entity entity) {
		if ((entity.getFlags() & Entity.NORMAL_MAPPED_BIT) != 0) {
			if ((entity.getFlags() & Entity.SKELETAL_BIT) != 0) {
				addEntity(entity, normalMappedSkeletalEntities);
			} else {
				addEntity(entity, normalMappedEntities);
			}
		} else {
			if ((entity.getFlags() & Entity.SKELETAL_BIT) != 0) {
				addEntity(entity, defaultSkeletalEntities);
			} else {
				addEntity(entity, defaultEntities);
			}
		}
	}
	
	private void addEntity(Entity entity, HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> entities) {
		Geometry geometry = entity.getGeometry();
		if (entities.containsKey(geometry)) {
			HashMap<Material, ArrayList<Entity>> nestedEntities = entities.get(geometry);
			addEntity(entity, entity.getMaterial(), nestedEntities);
		} else {
			HashMap<Material, ArrayList<Entity>> nestedEntities = new HashMap<>();
			addEntity(entity, entity.getMaterial(), nestedEntities);
			entities.put(geometry, nestedEntities);
		}
	}
	
	private void addEntity(Entity entity, Material material, HashMap<Material, ArrayList<Entity>> entities) {
		if (entities.containsKey(material)) {
			ArrayList<Entity> entityList = entities.get(material);
			entityList.add(entity);
		} else {
			ArrayList<Entity> entityList = new ArrayList<>();
			entityList.add(entity);
			entities.put(material, entityList);
		}
	}
	
	public void render(Camera camera, Vector3f cameraPosition) {
		pipeline.doGeometryPass(camera, defaultEntities, normalMappedEntities, defaultSkeletalEntities, normalMappedSkeletalEntities);
		pipeline.doLightingPass(camera, lights, cameraPosition);
		pipeline.doFinalRender();
	}

}

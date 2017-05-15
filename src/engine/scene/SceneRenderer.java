package engine.scene;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3f;

import engine.Camera;
import engine.CoreSettings;
import engine.GraphicsSettings;
import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.world.Component;
import engine.world.Entity;
import engine.world.Light;
import engine.world.Material;

public class SceneRenderer {
	
	private RenderingPipeline pipeline;
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultSkeletalEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedSkeletalEntities = new HashMap<>();
	
	private ArrayList<Light> lights = new ArrayList<>();
	
	private float lightLevel = 0.5f;
	
	public SceneRenderer(Renderer renderer, RenderingStrategy strategy, CoreSettings coreSettings, GraphicsSettings graphicsSettings) {
		switch (strategy) {
			case DEFERRED:
				pipeline = new DeferredRenderingPipeline(renderer, coreSettings, graphicsSettings);
				break;
		}
	}
	
	public float getLightLevel() {
		return lightLevel;
	}
	
	public void setLightLevel(float lightLevel) {
		this.lightLevel = lightLevel;
	}
	
	public void addLight(Light light) {
		lights.add(light);
	}
	
	public void addEntity(Entity entity) {
		MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
		if ((meshComponent.getFlags() & Component.NORMAL_MAPPED_BIT) != 0) {
			if ((meshComponent.getFlags() & Component.SKELETAL_BIT) != 0) {
				addEntity(entity, normalMappedSkeletalEntities);
			} else {
				addEntity(entity, normalMappedEntities);
			}
		} else {
			if ((meshComponent.getFlags() & Component.SKELETAL_BIT) != 0) {
				addEntity(entity, defaultSkeletalEntities);
			} else {
				addEntity(entity, defaultEntities);
			}
		}
	}
	
	private void addEntity(Entity entity, HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> entities) {
		MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
		Geometry geometry = meshComponent.getGeometry();
		if (entities.containsKey(geometry)) {
			HashMap<Material, ArrayList<Entity>> nestedEntities = entities.get(geometry);
			addEntity(entity, meshComponent.getMaterial(), nestedEntities);
		} else {
			HashMap<Material, ArrayList<Entity>> nestedEntities = new HashMap<>();
			addEntity(entity, meshComponent.getMaterial(), nestedEntities);
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
		pipeline.doLightingPass(lightLevel, camera, lights, cameraPosition);
		pipeline.doFinalRender();
	}

}

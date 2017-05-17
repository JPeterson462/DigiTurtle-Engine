package engine.world;

import java.util.ArrayList;
import java.util.HashMap;

import engine.rendering.Geometry;
import engine.scene.MeshComponent;

public class World {

	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultSkeletalEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedSkeletalEntities = new HashMap<>();
	
	private ArrayList<Light> lights = new ArrayList<>();

	public void addLight(Light light) {
		lights.add(light);
	}
	
	public boolean removeLight(Light light) {
		return lights.remove(light);
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

	public boolean removeEntity(Entity entity) {
		MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
		if ((meshComponent.getFlags() & Component.NORMAL_MAPPED_BIT) != 0) {
			if ((meshComponent.getFlags() & Component.SKELETAL_BIT) != 0) {
				return removeEntity(entity, normalMappedSkeletalEntities);
			} else {
				return removeEntity(entity, normalMappedEntities);
			}
		} else {
			if ((meshComponent.getFlags() & Component.SKELETAL_BIT) != 0) {
				return removeEntity(entity, defaultSkeletalEntities);
			} else {
				return removeEntity(entity, defaultEntities);
			}
		}
	}
	
	private boolean removeEntity(Entity entity, HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> entities) {
		MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
		if (entities.containsKey(meshComponent.getGeometry())) {
			HashMap<Material, ArrayList<Entity>> nestedEntities = entities.get(meshComponent.getGeometry());
			if (nestedEntities.containsKey(meshComponent.getMaterial())) {
				ArrayList<Entity> entityList = nestedEntities.get(meshComponent.getMaterial());
				return entityList.remove(entity);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> getDefaultEntities() {
		return defaultEntities;
	}

	public HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> getNormalMappedEntities() {
		return normalMappedEntities;
	}

	public HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> getDefaultSkeletalEntities() {
		return defaultSkeletalEntities;
	}

	public HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> getNormalMappedSkeletalEntities() {
		return normalMappedSkeletalEntities;
	}

	public ArrayList<Light> getLights() {
		return lights;
	}

}

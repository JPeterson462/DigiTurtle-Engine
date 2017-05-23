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
	
	private Terrain terrain;
	
	private float width, height, resolution;
	
	public World(float width, float height, float resolution) {
		this.width = width;
		this.height = height;
		this.resolution = resolution;
	}

	public void setTerrain(TerrainGenerator generator, float x, float z) {
		terrain = new Terrain(generator, Math.floorDiv((int) width, (int) resolution),  Math.floorDiv((int) height, (int) resolution), resolution, x, z);
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
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
		ArrayList<Geometry> geometryList = meshComponent.getGeometry();
		for (int i = 0; i < geometryList.size(); i++) {
			Geometry geometry = geometryList.get(i);
			if (entities.containsKey(geometry)) {
				HashMap<Material, ArrayList<Entity>> nestedEntities = entities.get(geometry);
				addEntity(entity, meshComponent.getMaterials(), nestedEntities);
			} else {
				HashMap<Material, ArrayList<Entity>> nestedEntities = new HashMap<>();
				addEntity(entity, meshComponent.getMaterials(), nestedEntities);
				entities.put(geometry, nestedEntities);
			}
		}
	}
	
	private void addEntity(Entity entity, ArrayList<Material> materials, HashMap<Material, ArrayList<Entity>> entities) {
		for (int i = 0; i < materials.size(); i++) {
			Material material = materials.get(i);
			if (entities.containsKey(material)) {
				ArrayList<Entity> entityList = entities.get(material);
				entityList.add(entity);
			} else {
				ArrayList<Entity> entityList = new ArrayList<>();
				entityList.add(entity);
				entities.put(material, entityList);
			}
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
		ArrayList<Geometry> geometryList = meshComponent.getGeometry();
		for (int i = 0; i < geometryList.size(); i++) {
			Geometry geometry = geometryList.get(i);
			if (entities.containsKey(geometry)) {
				HashMap<Material, ArrayList<Entity>> nestedEntities = entities.get(geometry);
				ArrayList<Material> materials = meshComponent.getMaterials();
				for (int j = 0; j < materials.size(); j++) {
					Material material = materials.get(j);
					if (nestedEntities.containsKey(material)) {
						ArrayList<Entity> entityList = nestedEntities.get(material);
						if (!entityList.remove(entity)) {
							return false;
						}
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
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

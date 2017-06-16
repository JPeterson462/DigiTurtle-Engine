package engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.rendering.Geometry;
import engine.scene.MeshComponent;
import utils.Receiver;

public class World {

	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultSkeletalEntities = new HashMap<>();
	
	private HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedSkeletalEntities = new HashMap<>();
	
	private ArrayList<Light> lights = new ArrayList<>();
	
	private TerrainChunk[][] terrain;
	
	private Skybox skybox;
	
	private float width, height, resolution;
	
	private Vector4f ambientLight = new Vector4f();
	
	public World(float chunkWidth, float chunkHeight, float resolution, int width, int height) {
		this.width = chunkWidth;
		this.height = chunkHeight;
		this.resolution = resolution;
		terrain = new TerrainChunk[width][height];
	}
	
	public Vector4f getAmbientLight() {
		return ambientLight;
	}
	
	public void setAmbientLight(Vector3f lightColor, float lightLevel) {
		ambientLight.set(lightColor, lightLevel);
	}
	
	public Skybox getSkybox() {
		return skybox;
	}
	
	public void setSkybox(Skybox skybox) {
		this.skybox = skybox;
	}
	
	public void forEachEntity(Receiver<Entity> receiver) {
		for (Map.Entry<Geometry, HashMap<Material, ArrayList<Entity>>> entityGroup : defaultEntities.entrySet()) {
			HashMap<Material, ArrayList<Entity>> entitySubgroup = entityGroup.getValue();
			for (Map.Entry<Material, ArrayList<Entity>> entityList : entitySubgroup.entrySet()) {
				ArrayList<Entity> list = entityList.getValue();
				for (int i = 0; i < list.size(); i++) {
					receiver.receive(list.get(i));
				}
			}
		}
		for (Map.Entry<Geometry, HashMap<Material, ArrayList<Entity>>> entityGroup : normalMappedEntities.entrySet()) {
			HashMap<Material, ArrayList<Entity>> entitySubgroup = entityGroup.getValue();
			for (Map.Entry<Material, ArrayList<Entity>> entityList : entitySubgroup.entrySet()) {
				ArrayList<Entity> list = entityList.getValue();
				for (int i = 0; i < list.size(); i++) {
					receiver.receive(list.get(i));
				}
			}
		}
		for (Map.Entry<Geometry, HashMap<Material, ArrayList<Entity>>> entityGroup : defaultSkeletalEntities.entrySet()) {
			HashMap<Material, ArrayList<Entity>> entitySubgroup = entityGroup.getValue();
			for (Map.Entry<Material, ArrayList<Entity>> entityList : entitySubgroup.entrySet()) {
				ArrayList<Entity> list = entityList.getValue();
				for (int i = 0; i < list.size(); i++) {
					receiver.receive(list.get(i));
				}
			}
		}
		for (Map.Entry<Geometry, HashMap<Material, ArrayList<Entity>>> entityGroup : normalMappedSkeletalEntities.entrySet()) {
			HashMap<Material, ArrayList<Entity>> entitySubgroup = entityGroup.getValue();
			for (Map.Entry<Material, ArrayList<Entity>> entityList : entitySubgroup.entrySet()) {
				ArrayList<Entity> list = entityList.getValue();
				for (int i = 0; i < list.size(); i++) {
					receiver.receive(list.get(i));
				}
			}
		}
	}

	public void setTerrain(int i, int j, TerrainGenerator generator, TerrainTexturePack texturePack, float shininess, float specularFactor) {
		float x = i * width - (terrain.length * width) / 2;
		float z = j * height - (terrain[0].length * height) / 2;
		terrain[i][j] = new TerrainChunk(generator, Math.floorDiv((int) width, (int) resolution),  
				Math.floorDiv((int) height, (int) resolution), resolution, x, z, texturePack);
		terrain[i][j].setShininess(shininess);
		terrain[i][j].setSpecularFactor(specularFactor);
	}
	
	public TerrainChunk[][] getTerrain() {
		return terrain;
	}
	
	public void addLight(Light light) {
		lights.add(light);
	}
	
	public boolean removeLight(Light light) {
		return lights.remove(light);
	}
	
	public void addEntity(Entity entity) {
		entity.setWorld(this);
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
		entity.setWorld(null);
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
	
	public void update(float dt) {
		skybox.update(dt);
		forEachEntity((entity) -> entity.update(dt));
	}

}

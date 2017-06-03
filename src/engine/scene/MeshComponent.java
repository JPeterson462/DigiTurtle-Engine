package engine.scene;

import java.util.ArrayList;

import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Vertex;
import engine.world.Component;
import engine.world.Entity;
import engine.world.Material;
import library.models.Mesh;
import library.models.Model;

public class MeshComponent implements Component {

	private int flags;

	private ArrayList<Geometry> geometry = new ArrayList<>();

	private ArrayList<Material> materials = new ArrayList<>();

	public MeshComponent(Geometry geometry, Material material, boolean normalMapped) {
		flags = (normalMapped ? NORMAL_MAPPED_BIT : 0);
		this.geometry.add(geometry);
		materials.add(material);
	}

	public MeshComponent(Model model, Renderer renderer, boolean normalMapped) {
		ArrayList<Mesh> meshes = model.getMeshes();
		for (int i = 0; i < meshes.size(); i++) {
			Mesh mesh = meshes.get(i);
			if (model.getSkeleton() != null) {
				flags = (normalMapped ? NORMAL_MAPPED_BIT : 0) | SKELETAL_BIT;	
				geometry.add(renderer.createGeometry(convertVertices(mesh.getVertices(), true), mesh.getIndices(), 
						Vertex.POSITION_BIT | Vertex.TEXTURE_COORD_BIT | Vertex.NORMAL_BIT | Vertex.JOINTID_BIT | Vertex.WEIGHT_BIT));
			} else {
				flags = (normalMapped ? NORMAL_MAPPED_BIT : 0);			
				geometry.add(renderer.createGeometry(convertVertices(mesh.getVertices(), false), mesh.getIndices(),
						Vertex.POSITION_BIT | Vertex.TEXTURE_COORD_BIT | Vertex.NORMAL_BIT));
			}
			Material material = new Material();
			material.setDiffuseTexture(renderer.createTexture(mesh.getMaterial().getDiffuseTexture(), true));
			if (mesh.getMaterial().getNormalTexture() != null) {
				material.setNormalTexture(renderer.createTexture(mesh.getMaterial().getNormalTexture(), false));
			}
			material.setReflectivity(mesh.getMaterial().getReflectivity());
			material.setShineDamper(mesh.getMaterial().getShineDamper());
			materials.add(material);
		}
	}

	public ArrayList<Vertex> convertVertices(ArrayList<library.models.Vertex> oldVertices, boolean skeletal) {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for (int i = 0; i < oldVertices.size(); i++) {
			Vertex vertex = new Vertex(oldVertices.get(i));
			if (skeletal) {
				vertex.jointIDs(oldVertices.get(i).getJointIds());
				vertex.weights(oldVertices.get(i).getWeights());
			}
			vertices.add(vertex);
		}
		return vertices;
	}

	public int getFlags() {
		return flags;
	}
	
	public ArrayList<Geometry> getGeometry() {
		return geometry;
	}
	
	public ArrayList<Material> getMaterials() {
		return materials;
	}

	@Override
	public void update(Entity entity, float delta) {

	}

}

package engine.scene;

import java.util.ArrayList;

import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Vertex;
import engine.world.Component;
import engine.world.Entity;
import engine.world.Material;
import library.models.Model;

public class MeshComponent implements Component {

	private int flags;

	private Geometry geometry;

	private Material material;

	public MeshComponent(Geometry geometry, Material material, boolean normalMapped) {
		flags = (normalMapped ? NORMAL_MAPPED_BIT : 0);
		this.geometry = geometry;
		this.material = material;
	}

	public MeshComponent(Model model, Renderer renderer, boolean normalMapped) {
		if (model.getSkeleton() != null) {
			flags = (normalMapped ? NORMAL_MAPPED_BIT : 0) | SKELETAL_BIT;	
			this.geometry = renderer.createGeometry(convertVertices(model.getVertices(), true), model.getIndices(), 
					Vertex.POSITION_BIT | Vertex.TEXTURE_COORD_BIT | Vertex.NORMAL_BIT | Vertex.JOINTID_BIT | Vertex.WEIGHT_BIT);
		} else {
			flags = (normalMapped ? NORMAL_MAPPED_BIT : 0);			
			this.geometry = renderer.createGeometry(convertVertices(model.getVertices(), false), model.getIndices(),
					Vertex.POSITION_BIT | Vertex.TEXTURE_COORD_BIT | Vertex.NORMAL_BIT);
		}
		this.material = model.getMaterial();
	}

	public ArrayList<Vertex> convertVertices(ArrayList<library.models.Vertex> oldVertices, boolean skeletal) {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for (int i = 0; i < oldVertices.size(); i++) {
			Vertex vertex = new Vertex(oldVertices.get(i));
			if (skeletal) {
				vertex.jointIDs(oldVertices.get(i).getJointIds());
				vertex.weights(oldVertices.get(i).getWeights());
			}
			System.out.println(vertex.position());
			vertices.add(vertex);
		}
		return vertices;
	}

	public int getFlags() {
		return flags;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public Material getMaterial() {
		return material;
	}

	@Override
	public void update(Entity entity, float delta) {

	}

}

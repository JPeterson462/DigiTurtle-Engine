package library.models;

import java.util.ArrayList;

public class Mesh {

	private ArrayList<Vertex> vertices;
	
	private ArrayList<Integer> indices;
	
	private Material material;
	
	public Mesh(ModelVertexQueue queue) {
		vertices = new ArrayList<>();
		for (int i = 0; i < queue.getVertices().size(); i++) {
			vertices.add(queue.transform(queue.getVertices().get(i)));
		}
		indices = queue.getIndices();
	}
	
	public Mesh(ArrayList<Vertex> vertices, ArrayList<Integer> indices) {
		this.vertices = vertices;
		this.indices = indices;
	}
	
	public ArrayList<Vertex> getVertices() {
		return vertices;
	}
	
	public ArrayList<Integer> getIndices() {
		return indices;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public Mesh setMaterial(Material material) {
		this.material = material;
		return this;
	}
	
}

package library.models.importers;

import java.io.InputStream;
import java.util.ArrayList;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import engine.AssetInputStream;
import engine.rendering.Renderer;
import library.models.Mesh;
import library.models.Model;
import library.models.ModelImporter;
import library.models.Vertex;

public class AssimpModelImporter implements ModelImporter {

	@Override
	public Model importModel(InputStream stream, String animation, Renderer renderer) {
		if (!(stream instanceof AssetInputStream)) {
			throw new IllegalArgumentException("Assimp can only load assets from an AssetInputStream");
		}
		int flags = Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenNormals | Assimp.aiProcess_ImproveCacheLocality
				| Assimp.aiProcess_SortByPType | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_OptimizeGraph
				| Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_TransformUVCoords | Assimp.aiProcess_GenUVCoords
				| Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_FlipUVs;
		AIScene scene = Assimp.aiImportFileEx(((AssetInputStream) stream).getPath(), flags, null);
		ArrayList<Mesh> meshes = new ArrayList<>();
		int numMeshes = scene.mNumMeshes();
		PointerBuffer meshPointers = scene.mMeshes();
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(meshPointers.get(i));
			ArrayList<Vertex> vertices = new ArrayList<>();
			// vertices
			AIVector3D.Buffer positions = aiMesh.mVertices();
			AIVector3D.Buffer normals = aiMesh.mNormals();
			AIVector3D.Buffer textureCoords = aiMesh.mTextureCoords(0);
			AIColor4D.Buffer colors = aiMesh.mColors(0);
			int numVertices = aiMesh.mNumVertices();
			for (int j = 0; j < numVertices; j++) {
				vertices.add(new Vertex(null, null, null, null, null));
			}
			ArrayList<Integer> indices = new ArrayList<>();
			// indices (Triangulate)
			Mesh mesh = new Mesh(vertices, indices);
			// material
			mesh.setMaterial(null);
		}
		Model model = new Model(meshes);
		// bones
		return model;
	}

	@Override
	public String[] getExtensions() {
		return null;
	}

}

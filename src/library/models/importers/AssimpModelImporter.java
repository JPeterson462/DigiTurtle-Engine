package library.models.importers;

import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMaterialProperty;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;

import com.esotericsoftware.minlog.Log;

import engine.AssetInputStream;
import engine.rendering.Renderer;
import library.models.Joint;
import library.models.Material;
import library.models.Mesh;
import library.models.Model;
import library.models.ModelImporter;
import library.models.Vertex;
import utils.RelativeStreamGenerator;

public class AssimpModelImporter implements ModelImporter {
	
	public AssimpModelImporter() {
		Log.info(Assimp.aiGetLegalString());
	}

	@Override
	public Model importModel(InputStream stream, String animation, Renderer renderer, RelativeStreamGenerator streamGenerator) {
		if (!(stream instanceof AssetInputStream)) {
			throw new IllegalArgumentException("Assimp can only load assets from an AssetInputStream");
		}
		int flags = Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenNormals | Assimp.aiProcess_ImproveCacheLocality
				| Assimp.aiProcess_SortByPType | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_OptimizeGraph
				| Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_TransformUVCoords | Assimp.aiProcess_GenUVCoords
				| Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_FlipUVs;
		AIScene scene = Assimp.aiImportFileEx(((AssetInputStream) stream).getFullPath(), flags, null);
		if (scene == null) {
			Log.warn(Assimp.aiGetErrorString());
		}
		ArrayList<Mesh> meshes = new ArrayList<>();
		int numMeshes = scene.mNumMeshes();
		PointerBuffer meshPointers = scene.mMeshes();
		Joint root = new Joint(0, "root", new Matrix4f());
		int numJoints = 1;
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(meshPointers.get(i));
			PointerBuffer aiBones = aiMesh.mBones();
			AIBone[] bones = new AIBone[aiMesh.mNumBones()];
			AIVertexWeight.Buffer[] weights = new AIVertexWeight.Buffer[bones.length];
			// bones
			for (int j = 0; j < bones.length; j++) {
				bones[j] = AIBone.create(aiBones.get(j));
				AIMatrix4x4 transform = bones[j].mOffsetMatrix();
				Matrix4f matrix = new Matrix4f(
					transform.a1(), transform.b1(), transform.c1(), transform.d1(),
					transform.a2(), transform.b2(), transform.c2(), transform.d2(),
					transform.a3(), transform.b3(), transform.c3(), transform.d3(),
					transform.a4(), transform.b4(), transform.c4(), transform.d4()
				);
				root.addChild(new Joint(numJoints, "joint" + j, matrix));
				numJoints++;
				weights[j] = bones[j].mWeights();
			}
			ArrayList<Vertex> vertices = new ArrayList<>();
			// vertices
			AIVector3D.Buffer positions = aiMesh.mVertices();
			AIVector3D.Buffer normals = aiMesh.mNormals();
			AIVector3D.Buffer textureCoords = aiMesh.mTextureCoords(0);
//			AIColor4D.Buffer colors = aiMesh.mColors(0);
			int numVertices = aiMesh.mNumVertices();
			for (int j = 0; j < numVertices; j++) {
				int component = 0;
				Vector3i jointIds = new Vector3i();
				Vector3f jointWeights = new Vector3f();
				for (int k = 0; k < aiMesh.mNumBones(); k++) {
					for (int l = 0; l < bones[k].mNumWeights(); l++) {
						AIVertexWeight weight = weights[k].get(l);
						if (weight.mVertexId() == j) {
							if (component == 0) {
								jointIds.x = k;
								jointWeights.x = weight.mWeight();
							}
							else if (component == 1) {
								jointIds.y = k;
								jointWeights.y = weight.mWeight();
							}
							else if (component == 2) {
								jointIds.z = k;
								jointWeights.z = weight.mWeight();
							}
							component++;
						}
					}
				}
				Vector3f position = new Vector3f(positions.get(j).x(), positions.get(j).y(), positions.get(j).z());
				Vector2f textureCoord = new Vector2f(textureCoords.get(j).x(), textureCoords.get(j).y());
				Vector3f normal = new Vector3f(normals.get(j).x(), normals.get(j).y(), normals.get(j).z());
				vertices.add(new Vertex(position, textureCoord, normal, jointIds, jointWeights));
			}
			ArrayList<Integer> indices = new ArrayList<>();
			// indices
			AIFace.Buffer faces = aiMesh.mFaces();
			for (int j = 0; j < aiMesh.mNumFaces(); j++) {
				AIFace face = faces.get(j);
				IntBuffer faceIndices = face.mIndices();
				for (int k = faceIndices.position(); k < faceIndices.limit(); k++) {
					indices.add(faceIndices.get(k));
				}
			}
			Mesh mesh = new Mesh(vertices, indices);
			AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(aiMesh.mMaterialIndex()));
			Material material = new Material();
			for (int j = 0; j < aiMaterial.mNumProperties(); j++) {
				AIMaterialProperty aiMaterialProperty = AIMaterialProperty.create(aiMaterial.mProperties().get(j));
				if (aiMaterialProperty.mKey().dataString().equalsIgnoreCase(Assimp.AI_MATKEY_SHININESS)) {
					material.setShininess(aiMaterialProperty.mData().getFloat(0));
				}
			}
			IntBuffer iflags = BufferUtils.createIntBuffer(1);
			AIString string = AIString.create();
			if (Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, string, null, null, null, null, null, iflags) == Assimp.aiReturn_SUCCESS) {
				material.setDiffuseTexture(streamGenerator.getRelativeStream(string.dataString()));
			}
			if (Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_NORMALS, 0, string, null, null, null, null, null, iflags) == Assimp.aiReturn_SUCCESS) {
				material.setNormalTexture(streamGenerator.getRelativeStream(string.dataString()));
			}
			mesh.setMaterial(material);
		}
		Model model = new Model(meshes);
		model.setSkeleton(root, numJoints);
		return model;
	}

	@Override
	public String[] getExtensions() {
		return new String[0];
	}

}

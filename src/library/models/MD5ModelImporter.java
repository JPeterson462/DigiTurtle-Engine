package library.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import com.esotericsoftware.minlog.Log;

import engine.AssetInputStream;
import engine.rendering.Renderer;
import engine.world.Material;
import utils.AtomicObject;

public class MD5ModelImporter implements ModelImporter {
	
	private static final int MAX_WEIGHTS = 3;
	
	private String[] EXTENSIONS = { "md5mesh" };
	
	//TODO compute normals
	
	class MD5Vertex {
		Vector2f textureCoord;
		int startWeight, weightCount;
	}
	
	class MD5Weight {
		int jointIndex;
		float bias;
		Vector3f position;
	}

	@Override
	public Model importModel(InputStream stream, String animation, Renderer renderer) {
		Model model = new Model();
		final int DEFAULT_SECTION = 0, MESH_SECTION = 1, JOINTS_SECTION = 2;
		AtomicInteger section = new AtomicInteger(DEFAULT_SECTION);
		AtomicInteger meshSection = new AtomicInteger();
		AtomicObject<MD5Vertex[]> md5VerticesPointer = new AtomicObject<>();
		AtomicObject<MD5Weight[]> md5WeightsPointer = new AtomicObject<>();
		AtomicObject<ArrayList<Vertex>> verticesPointer = new AtomicObject<>();
		AtomicObject<ArrayList<Integer>> indicesPointer = new AtomicObject<>();
		AtomicObject<Material> materialPointer = new AtomicObject<>();
		AtomicInteger rootJoint = new AtomicInteger();
		ArrayList<Joint> joints = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			reader.lines().forEach(line -> {
				line = line.trim();
				String[] parts = line.split("[\\s]+");
				if (section.get() == DEFAULT_SECTION) {
					if (line.startsWith("MD5Version ")) {
						if (!parts[1].equalsIgnoreCase("10")) {
							Log.error("Invalid Version");
						}
					}
					else if (line.startsWith("numJoints")) {
						// Ignore
					}
					else if (line.startsWith("numMeshes")) {
						// Ignore
					}
					else if (line.startsWith("joints {")) {
						section.set(JOINTS_SECTION);
					}
					else if (line.startsWith("mesh {")) {
						section.set(MESH_SECTION);
						meshSection.set(0);
						verticesPointer.set(new ArrayList<>());
						indicesPointer.set(new ArrayList<>());
					}
				}
				else if (section.get() == MESH_SECTION) {
					if (line.startsWith("}")) {
						for (int i = 0; i < md5VerticesPointer.get().length; i++) {
							Vector3f position = new Vector3f();
							MD5Vertex md5Vertex = md5VerticesPointer.get()[i];
							Vector3f normal = new Vector3f();
							Vector3i jointIndices = new Vector3i();
							Vector3f weights = new Vector3f();
							Vector3f[] positions = new Vector3f[MAX_WEIGHTS];
							for (int j = 0; j < md5Vertex.weightCount && j < MAX_WEIGHTS; j++) {
								MD5Weight weight = md5WeightsPointer.get()[md5Vertex.startWeight + j];
								Joint joint = joints.get(weight.jointIndex);
								Vector4f rotatedPos = new Vector4f(weight.position, 1.0f);
								joint.getBindLocalTransform().transform(rotatedPos);
								positions[j] = new Vector3f(rotatedPos.x, rotatedPos.y, rotatedPos.z);
								if (j == 0) {
									jointIndices.x = md5Vertex.startWeight + j;
									weights.x = weight.bias;
								}
								else if (j == 1) {
									jointIndices.y = md5Vertex.startWeight + j;
									weights.y = weight.bias;
								}
								else if (j == 2) {
									jointIndices.z = md5Vertex.startWeight + j;
									weights.z = weight.bias;
								}
							}
							float sum = weights.x + weights.y + weights.z;
							for (int j = 0; j < md5Vertex.weightCount && j < MAX_WEIGHTS; j++) {
								float weight = 0;
								if (j == 0) {
									weight = weights.x;
								}
								else if (j == 1) {
									weight = weights.y;
								}
								else if (j == 2) {
									weight = weights.z;
								}
								position.fma(weight, positions[j]);
							}
							weights.mul(1f / sum);
							verticesPointer.get().add(new Vertex(position, md5Vertex.textureCoord, normal, jointIndices, weights));
						}
						model.addMesh(new Mesh(verticesPointer.get(), indicesPointer.get()).setMaterial(materialPointer.get()));
						verticesPointer.set(new ArrayList<>());
						indicesPointer.set(new ArrayList<>());
						section.set(DEFAULT_SECTION);
					} else {
						if (meshSection.get() == 0) {
							if (line.startsWith("shader ")) {
								InputStream diffuseTexture = findTexture(line.substring("shader ".length()), stream);
								Material material = new Material();
								material.setDiffuseTexture(renderer.createTexture(diffuseTexture, true));
								materialPointer.set(material);
							}
							else if (line.startsWith("numverts ")) {
								md5VerticesPointer.set(new MD5Vertex[Integer.parseInt(parts[1])]);
								meshSection.incrementAndGet();
							}
						}
						else if (meshSection.get() == 1) {
							if (line.startsWith("vert ")) {
								MD5Vertex vertex = new MD5Vertex();
								vertex.textureCoord = new Vector2f(Float.parseFloat(parts[3]), Float.parseFloat(parts[4]));
								vertex.startWeight = Integer.parseInt(parts[6]);
								vertex.weightCount = Integer.parseInt(parts[7]);
								md5VerticesPointer.get()[Integer.parseInt(parts[1])] = vertex;
							}
							else if (line.startsWith("numtris ")) {
								meshSection.incrementAndGet();
							}
						}
						else if (meshSection.get() == 2) {
							if (line.startsWith("tri ")) {
								indicesPointer.get().add(Integer.parseInt(parts[2]));
								indicesPointer.get().add(Integer.parseInt(parts[3]));
								indicesPointer.get().add(Integer.parseInt(parts[4]));
							}
							else if (line.startsWith("numweights ")) {
								md5WeightsPointer.set(new MD5Weight[Integer.parseInt(parts[1])]);
								meshSection.incrementAndGet();
							}
						}
						else if (meshSection.get() == 3) {
							if (line.startsWith("weight ")) {
								MD5Weight weight = new MD5Weight();
								weight.jointIndex = Integer.parseInt(parts[2]);
								weight.bias = Float.parseFloat(parts[3]);
								weight.position = new Vector3f(Float.parseFloat(parts[5]), Float.parseFloat(parts[6]), Float.parseFloat(parts[7]));
								md5WeightsPointer.get()[Integer.parseInt(parts[1])] = weight;
							}
						}
					}
				}
				else if (section.get() == JOINTS_SECTION) {
					if (line.startsWith("}")) {
						model.setSkeleton(joints.get(rootJoint.get()), joints.size());
						section.set(DEFAULT_SECTION);
					} else {
						int parentIndex = Integer.parseInt(parts[1]);
						Vector3f position = new Vector3f(Float.parseFloat(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
						Quaternionf orientation = new Quaternionf(Float.parseFloat(parts[8]), Float.parseFloat(parts[9]), Float.parseFloat(parts[10]));
						Matrix4f transform = new Matrix4f().translationRotate(position.x, position.y, position.z, orientation);
						Joint joint = new Joint(joints.size(), unquote(parts[0]), transform);
						joints.add(joint);
						if (parentIndex > -1) {
							Joint parent = joints.get(parentIndex);
							parent.addChild(joint);
						} else {
							rootJoint.set(joint.getIndex());
						}
					}
				}
			});
			return model;
		} catch (IOException e) {
			Log.error("Failed to import MD5 file. " + stream, e);
			return null;
		}
	}
	
	private InputStream findTexture(String text, InputStream stream) {
		text = unquote(text);
		String path = text.indexOf('.') > 0 ? text : text + ".tga";
		if (!(stream instanceof AssetInputStream)) {
			return null;
		}
		return ((AssetInputStream) stream).getRelativeStream(path);
	}
	
	private String unquote(String text) {
		return text.substring(1, text.length() - 1);
	}

	@Override
	public String[] getExtensions() {
		return EXTENSIONS;
	}

}

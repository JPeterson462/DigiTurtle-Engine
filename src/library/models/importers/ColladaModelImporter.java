package library.models.importers;

import java.io.InputStream;
import java.util.ArrayList;

import org.joml.Vector2f;

import engine.rendering.Renderer;
import library.models.Joint;
import library.models.Mesh;
import library.models.Model;
import library.models.ModelImporter;
import library.models.Vertex;
import library.models.collada.ColladaJoint;
import library.models.collada.ColladaModel;
import library.models.collada.ColladaSkinning;
import library.models.collada.ColladaVertex;
import library.models.collada.GeometryLoader;
import library.models.collada.SkeletonLoader;
import library.models.collada.SkinLoader;
import utils.XMLNode;
import utils.XMLParser;

public class ColladaModelImporter implements ModelImporter {

	private String[] EXTENSIONS = { "dae" };
	
	private static final int MAX_WEIGHTS = 3;

	@Override
	public String[] getExtensions() {
		return EXTENSIONS;
	}

	@Override
	public Model importModel(InputStream stream, String animation, Renderer renderer) {
		XMLNode node = XMLParser.loadXmlFile(stream);
		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), MAX_WEIGHTS);
		ColladaSkinning skinningData = skinLoader.extractSkinData();
		ColladaModel model = new ColladaModel();
		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.getJointOrder(), animation);
		jointsLoader.extractBoneData(model);
		GeometryLoader geometryLoader = new GeometryLoader(node.getChild("library_geometries"), skinningData.getVerticesSkinData());
		geometryLoader.extractModelData(model);
		ArrayList<Vertex> vertices = new ArrayList<>();
		for (int i = 0; i < model.getVertices().size(); i++) {
			ColladaVertex vertex = model.getVertices().get(i);
			vertices.add(new Vertex(vertex.getPosition(), flipY(vertex), vertex.getNormal(), vertex.getJointIds(), vertex.getVertexWeights()));
		}
		ArrayList<Integer> indices = new ArrayList<>();
		indices.addAll(model.getIndices());
		Model standardModel = new Model(new Mesh(vertices, indices));
		Joint rootJoint = convertJoint(model.getRootJoint());
		standardModel.setSkeleton(rootJoint, model.getJointCount());
		return standardModel;
	}
	
	private Vector2f flipY(ColladaVertex vertex) {
		return new Vector2f(vertex.getTextureCoord().x, 1f - vertex.getTextureCoord().y);
	}
	
	private Joint convertJoint(ColladaJoint colladaJoint) {
		Joint joint = new Joint(colladaJoint.getIndex(), colladaJoint.getName(), colladaJoint.getBindLocalTransform());
		for (int i = 0; i < colladaJoint.getChildren().size(); i++) {
			joint.addChild(convertJoint(colladaJoint.getChildren().get(i)));
		}
		return joint;
	}

}

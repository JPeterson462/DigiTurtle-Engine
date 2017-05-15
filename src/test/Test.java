package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.esotericsoftware.minlog.Log;

import engine.CoreSettings;
import engine.FirstPersonCamera;
import engine.GraphicsSettings;
import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import engine.rendering.opengl.GLRenderer;
import engine.scene.AmbientLight;
import engine.scene.Entity;
import engine.scene.Material;
import engine.scene.PointLight;
import engine.scene.RenderingStrategy;
import engine.scene.Scene;

public class Test {
	
	private static Texture texture;
	
	private static Geometry geometry;
	
	private static Scene scene;
	
	private static FirstPersonCamera camera;
	
	private static Vector3f cameraPosition = new Vector3f();
	
	private static double t = 0;
	
	private static Entity entity;
	
	private static void face(ArrayList<Integer> indices, int i0, int i1, int i2, int i3) {
		indices.add(i0);
		indices.add(i1);
		indices.add(i2);
		indices.add(i2);
		indices.add(i3);
		indices.add(i0);
	}
	
	public static void main(String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		Renderer renderer = new GLRenderer();
		CoreSettings coreSettings = new CoreSettings();
		
		GraphicsSettings graphicsSettings = new GraphicsSettings();
		
		final float SIZE = 10;
		renderer.createContext(coreSettings, graphicsSettings, () -> {
			try {
				texture = renderer.createTexture(new FileInputStream("Rabbit_D.png"), false);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			//float n = 0.5773502692f;
			ArrayList<Vertex> vertices = new ArrayList<>();
			vertices.add(new Vertex().position(-SIZE/2, -SIZE/2, -SIZE/2).textureCoord(0, 0).normal(0, 0, 1));
			vertices.add(new Vertex().position(SIZE/2, -SIZE/2, -SIZE/2).textureCoord(1, 0).normal(0, 0, 1));
			vertices.add(new Vertex().position(SIZE/2, SIZE/2, -SIZE/2).textureCoord(1, 1).normal(0, 0, 1));
			vertices.add(new Vertex().position(-SIZE/2, SIZE/2, -SIZE/2).textureCoord(0, 1).normal(0, 0, 1));
			vertices.add(new Vertex().position(-SIZE/2, -SIZE/2, SIZE/2).textureCoord(0, 0).normal(0, 0, 1));
			vertices.add(new Vertex().position(SIZE/2, -SIZE/2, SIZE/2).textureCoord(1, 0).normal(0, 0, 1));
			vertices.add(new Vertex().position(SIZE/2, SIZE/2, SIZE/2).textureCoord(1, 1).normal(0, 0, 1));
			vertices.add(new Vertex().position(-SIZE/2, SIZE/2, SIZE/2).textureCoord(0, 1).normal(0, 0, 1));
			ArrayList<Integer> indices = new ArrayList<>();
			face(indices, 0, 1, 2, 3);
			face(indices, 4, 5, 6, 7);
			face(indices, 0, 1, 5, 4);
			face(indices, 3, 2, 6, 7);
			face(indices, 1, 5, 6, 2);
			face(indices, 0, 4, 7, 3);
			geometry = renderer.createGeometry(vertices, indices, Vertex.POSITION_BIT | Vertex.TEXTURE_COORD_BIT | Vertex.NORMAL_BIT);
			
			Material material = new Material();
			material.setDiffuseTexture(texture);
			camera = new FirstPersonCamera(coreSettings, graphicsSettings);
			scene = new Scene(renderer, RenderingStrategy.DEFERRED, coreSettings, graphicsSettings);
			entity = new Entity(geometry, material, false);
			scene.addEntity(entity);
			scene.addLight(new AmbientLight(1, 1, 1));
			PointLight pointLight = new PointLight(0, 1, 0);
			pointLight.setRange(5);
			pointLight.setPosition(0, 0, 6);
			scene.addLight(pointLight);
			scene.setLightLevel(0.7f);
		});
		while (renderer.validContext()) {
			float dt = renderer.getDeltaTime();
			renderer.prepareContext();
			
			t += 270 * dt;
			float dist = 15;
			
			entity.setOrientation(new Quaternionf().rotationY((float) Math.toRadians(t)));
			
			cameraPosition.set(0, 0, dist);
//			cameraPosition.set(dist * (float) Math.cos(Math.toRadians(t)) + 5, dist, dist * (float) Math.sin(Math.toRadians(t)) + 5);
			camera.getViewMatrix().setLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z, 0, 0, 0, 0, 1, 0);
			
			scene.render(camera, cameraPosition);
			
			renderer.updateContext();
		}
		renderer.destroyContext(() -> {
			//cleanup
		});
	}

}

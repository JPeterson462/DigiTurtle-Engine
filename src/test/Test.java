package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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
import engine.scene.Entity;
import engine.scene.Material;
import engine.scene.RenderingStrategy;
import engine.scene.Scene;

public class Test {
	
	private static Texture texture;
	
	private static Geometry geometry;
	
	private static Scene scene;
	
	private static FirstPersonCamera camera;
	
	private static Vector3f cameraPosition = new Vector3f();
	
	private static double t = 0;
	
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
		renderer.createContext(coreSettings, graphicsSettings, () -> {
			try {
				texture = renderer.createTexture(new FileInputStream("Rabbit_D.png"), false);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			ArrayList<Vertex> vertices = new ArrayList<>();
			final float SIZE = 10;
			vertices.add(new Vertex().position(0, 0, 0).textureCoord(0, 0).normal(1, 0, 0));
			vertices.add(new Vertex().position(SIZE, 0, 0).textureCoord(1, 0).normal(1, 0, 0));
			vertices.add(new Vertex().position(SIZE, SIZE, 0).textureCoord(1, 1).normal(1, 0, 0));
			vertices.add(new Vertex().position(0, SIZE, 0).textureCoord(0, 1).normal(1, 0, 0));
			vertices.add(new Vertex().position(0, 0, SIZE).textureCoord(0, 0).normal(1, 0, 0));
			vertices.add(new Vertex().position(SIZE, 0, SIZE).textureCoord(1, 0).normal(1, 0, 0));
			vertices.add(new Vertex().position(SIZE, SIZE, SIZE).textureCoord(1, 1).normal(1, 0, 0));
			vertices.add(new Vertex().position(0, SIZE, SIZE).textureCoord(0, 1).normal(1, 0, 0));
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
			scene = new Scene(renderer, RenderingStrategy.DEFERRED);
			Entity entity = new Entity(geometry, material, false);
			scene.addEntity(entity);
		});
		while (renderer.validContext()) {
			float dt = renderer.getDeltaTime();
			renderer.prepareContext();
			
			t += 270 * dt;
			float dist = 20;
			
			cameraPosition.set(dist * (float) Math.cos(Math.toRadians(t)) + 5, dist, dist * (float) Math.sin(Math.toRadians(t)) + 5);
			camera.getViewMatrix().setLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z, 5, 5, 5, 0, 1, 0);
			
			scene.render(camera, cameraPosition);
			
			renderer.updateContext();
		}
		renderer.destroyContext(() -> {
			//cleanup
		});
	}

}

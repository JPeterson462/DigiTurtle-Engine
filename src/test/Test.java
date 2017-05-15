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
import engine.scene.MeshComponent;
import engine.scene.RenderingStrategy;
import engine.scene.SceneRenderer;
import engine.skeleton.AnimationComponent;
import engine.skeleton.SkeletonComponent;
import engine.world.AmbientLight;
import engine.world.Entity;
import engine.world.Material;
import engine.world.PointLight;
import library.models.Animation;
import library.models.AnimationImporterLibrary;
import library.models.ColladaAnimationImporter;
import library.models.ColladaModelImporter;
import library.models.Model;
import library.models.ModelImporterLibrary;
import library.models.OBJModelImporter;

public class Test {
	
	private static Texture texture;
	
	private static Geometry geometry;
	
	private static SceneRenderer scene;
	
	private static FirstPersonCamera camera;
	
	private static Vector3f cameraPosition = new Vector3f();
	
	private static double t = 0;
	
	private static Entity entity;
	
	public static void main(String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		Renderer renderer = new GLRenderer();
		CoreSettings coreSettings = new CoreSettings();
		
		GraphicsSettings graphicsSettings = new GraphicsSettings();
		
		renderer.createContext(coreSettings, graphicsSettings, () -> {
			texture = renderer.createTexture(Test.class.getResourceAsStream("animatedDiffuse.png"), false);
//			texture = renderer.createTexture(Test.class.getResourceAsStream("crate.png"), false);
			Texture texture2 = renderer.createTexture(Test.class.getResourceAsStream("crateNormal.png"), false);
			
			ModelImporterLibrary.registerImporter(new OBJModelImporter());
			ModelImporterLibrary.registerImporter(new ColladaModelImporter());
			AnimationImporterLibrary.registerImporter(new ColladaAnimationImporter());
			
			Material material = new Material();
			material.setDiffuseTexture(texture);
//			material.setNormalTexture(texture2);
			camera = new FirstPersonCamera(coreSettings, graphicsSettings);
			scene = new SceneRenderer(renderer, RenderingStrategy.DEFERRED, coreSettings, graphicsSettings);
			//MeshComponent mesh = new MeshComponent(geometry, material, false);
			
//			Model model = ModelImporterLibrary.findImporter("obj").importModel(Test.class.getResourceAsStream("crate.obj"), null);
//			model.setMaterial(material);
//			MeshComponent mesh = new MeshComponent(model, renderer, true);
			entity = new Entity();
			entity.setPosition(new Vector3f(0, 0, 0));
//			entity.addComponent(mesh);
//			scene.addEntity(entity);
			
			Model model = ModelImporterLibrary.findImporter("dae").importModel(Test.class.getResourceAsStream("model.dae"), "Armature");
			model.setMaterial(material);
			Animation animation = AnimationImporterLibrary.findImporter("dae").importAnimation(Test.class.getResourceAsStream("model.dae"), "Armature");
			entity.addComponent(new MeshComponent(model, renderer, false));
			entity.addComponent(new SkeletonComponent(model));
			entity.addComponent(new AnimationComponent());
			entity.getComponent(AnimationComponent.class).doAnimation(animation);
			
			scene.addEntity(entity);
			
			scene.addLight(new AmbientLight(1, 1, 1));
			PointLight pointLight = new PointLight(1, 1, 1);
			pointLight.setRange(100);
			pointLight.setPosition(0, 0, 100);
			scene.addLight(pointLight);
			scene.setLightLevel(1f);
		});
		while (renderer.validContext()) {
			float dt = renderer.getDeltaTime();
			renderer.prepareContext();
			
			t += 180 * dt;
			float dist = 20;
			
			entity.setOrientation(new Quaternionf().rotationY((float) Math.toRadians(t)));
			
			cameraPosition.set(0, 0, dist);
//			cameraPosition.set(dist * (float) Math.cos(Math.toRadians(t)) + 5, dist, dist * (float) Math.sin(Math.toRadians(t)) + 5);
			camera.getViewMatrix().setLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z, 0, 0, 0, 0, 1, 0);
			
			entity.update(dt);
			
			scene.render(camera, cameraPosition);
			
			renderer.updateContext();
		}
		renderer.destroyContext(() -> {
			//cleanup
		});
	}

}

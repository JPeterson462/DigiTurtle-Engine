package test;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.esotericsoftware.minlog.Log;

import engine.CoreSettings;
import engine.FirstPersonCamera;
import engine.GraphicsSettings;
import engine.Importers;
import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Texture;
import engine.rendering.opengl.GLRenderer;
import engine.scene.MeshComponent;
import engine.scene.RenderingStrategy;
import engine.scene.SceneRenderer;
import engine.skeleton.AnimationComponent;
import engine.skeleton.SkeletonComponent;
import engine.sound.Music;
import engine.sound.SoundSystem;
import engine.sound.openal.ALSoundSystem;
import engine.text.TextBuffer;
import engine.text.TextEffects;
import engine.text.TextRenderer;
import engine.text.opengl.GLTextRenderer;
import engine.world.AmbientLight;
import engine.world.Entity;
import engine.world.Material;
import engine.world.SpotLight;
import engine.world.World;
import library.audio.AudioData;
import library.audio.AudioDecoderLibrary;
import library.audio.AudioStream;
import library.font.Font;
import library.font.FontImporterLibrary;
import library.models.Animation;
import library.models.AnimationImporterLibrary;
import library.models.Model;
import library.models.ModelImporterLibrary;

public class Test {
	
	private static Texture texture;
	
	private static Geometry geometry;
	
	private static SceneRenderer scene;
	
	private static FirstPersonCamera camera;
	
	private static Vector3f cameraPosition = new Vector3f();
	
	private static double t = 0;
	
	private static Entity entity;
	
	private static World world;
	private static SpotLight spotLight;
	
	private static SoundSystem soundSystem;
	
	private static Music music;
	
	private static Font font;
	private static TextBuffer buffer;
	private static TextRenderer textRenderer;
	
	public static void main(String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		Renderer renderer = new GLRenderer();
		CoreSettings coreSettings = new CoreSettings();
		
		GraphicsSettings graphicsSettings = new GraphicsSettings();
		
		renderer.createContext(coreSettings, graphicsSettings, () -> {
			Importers.register();
			
			font = FontImporterLibrary.findImporter("fnt").importFont(Test.class.getResourceAsStream("Consolas_small.fnt"), 32, (file) -> Test.class.getResourceAsStream(file));
			
			textRenderer = new GLTextRenderer(coreSettings.width, coreSettings.height);
			buffer = textRenderer.createBuffer();
			buffer.setFont(font);
			buffer.setBounds(400, 300);
			buffer.setPosition(new Vector2f(100, 100));
			buffer.setText("Hello\t\t\tWorld\nHello,\t\tTest");
			buffer.setEffect(TextEffects.newOutlineEffect(new Vector3f(1, 1, 1), new Vector3f(1, 0, 0), 0.5f, 0.7f));
			
			texture = renderer.createTexture(Test.class.getResourceAsStream("animatedDiffuse.png"), false);
//			texture = renderer.createTexture(Test.class.getResourceAsStream("crate.png"), false);
			Texture texture2 = renderer.createTexture(Test.class.getResourceAsStream("crateNormal.png"), false);
			
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
			
			world = new World();
			
			world.addEntity(entity);
			
			world.addLight(new AmbientLight(1, 1, 1));
//			PointLight pointLight = new PointLight(0, 0, 1);
//			pointLight.setRange(20);
//			pointLight.setPosition(0, 25, 4);
//			world.addLight(pointLight);
//			DirectionalLight directionalLight = new DirectionalLight(1, 1, 0);
//			directionalLight.setDirection(-1, -1, -1);
//			world.addLight(directionalLight);
			spotLight = new SpotLight(0, 1, 0);
			spotLight.setRange(30);
			spotLight.setPosition(0, 25, 25);
			spotLight.setAngle((float) Math.PI / 180f);
			spotLight.setDirection(0, 1, 0);
			world.addLight(spotLight);
			scene.setLightLevel(0.6f);
			
			soundSystem = new ALSoundSystem();
			soundSystem.createContext();
			
			AudioData data = new AudioData();
			AudioStream stream = AudioDecoderLibrary.findDecoder("ogx").openStream(Test.class.getResourceAsStream("01_Critical_Acclaim.ogx.ogg"), data);
			music = soundSystem.createMusic(stream, data);
			music.setLooping(true);
			music.play();
		});
		while (renderer.validContext()) {
			float dt = renderer.getDeltaTime();
			renderer.prepareContext();
			
			t += 180 * dt;
			float dist = 50;
			
			entity.setScale(new Vector3f(5));
//			entity.setOrientation(new Quaternionf().rotationY((float) Math.toRadians(t)));
			
			Vector3f v = new Vector3f(10 * (float) Math.cos(Math.toRadians(t)), 0, 10 * (float) Math.sin(Math.toRadians(t)));
			spotLight.setDirection(v);
			
			float height = 25;
			cameraPosition.set(0, height, dist);
//			cameraPosition.set(dist * (float) Math.cos(Math.toRadians(t)) + 5, dist, dist * (float) Math.sin(Math.toRadians(t)) + 5);
			camera.getViewMatrix().setLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z, 0, height, 0, 0, 1, 0);
			
			entity.update(dt);
			
			soundSystem.checkError();
			music.update();
			
			scene.render(camera, cameraPosition, world);
			
			buffer.render();
			
			renderer.updateContext();
		}
		renderer.destroyContext(() -> {
			music.delete();
			soundSystem.destroyContext();
			
			buffer.delete();
			//cleanup
		});
	}
	
}

package test;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;

import engine.AssetInputStream;
import engine.CoreSettings;
import engine.FirstPersonCamera;
import engine.GraphicsSettings;
import engine.Importers;
import engine.effects.BasicParticleEmitter;
import engine.effects.ParticleEmitter;
import engine.effects.ParticleRenderer;
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
import engine.world.EntityTag;
import engine.world.PointLight;
import engine.world.Skybox;
import engine.world.SkyboxBlender;
import engine.world.SpotLight;
import engine.world.TerrainGenerator;
import engine.world.TerrainTexturePack;
import engine.world.World;
import engine.world.physics.PhysicalComponent;
import engine.world.physics.PolyhedronBounds;
import engine.world.physics.PolyhedronBuilder;
import library.audio.AudioData;
import library.audio.AudioDecoderLibrary;
import library.audio.AudioStream;
import library.font.Font;
import library.font.FontImporterLibrary;
import library.models.Animation;
import library.models.AnimationImporterLibrary;
import library.models.Material;
import library.models.Model;
import library.models.ModelImporterLibrary;
import utils.OpenSimplexNoise;

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
	
	private static ParticleRenderer particleRenderer;
	
	public static void main(String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		Renderer renderer = new GLRenderer();
		CoreSettings coreSettings = new CoreSettings();
		
		GraphicsSettings graphicsSettings = new GraphicsSettings();
		graphicsSettings.anisotropicFiltering = true;
		
		renderer.createContext(coreSettings, graphicsSettings, () -> {
			Importers.register();
			
			font = FontImporterLibrary.findImporter("fnt").importFont(new AssetInputStream("Consolas_small.fnt"), 32, (file) -> new AssetInputStream(file));
			
			textRenderer = new GLTextRenderer(coreSettings.width, coreSettings.height);
			buffer = textRenderer.createBuffer();
			buffer.setFont(font);
			buffer.setBounds(400, 300);
			buffer.setPosition(new Vector2f(100, 100));
			buffer.setText("Hello\t\t\tWorld\nHello,\t\tTest");
			buffer.setEffect(TextEffects.newOutlineEffect(new Vector3f(1, 1, 1), new Vector3f(1, 0, 0), 0.5f, 0.7f));
			
//			texture = renderer.createTexture(new AssetInputStream("animatedDiffuse.png"), false);
//			texture = renderer.createTexture(new AssetInputStream("crate.png"), false);
			Texture texture2 = renderer.createTexture(new AssetInputStream("crateNormal.png"), false);
			
			Material material = new Material();
			material.setDiffuseTexture(new AssetInputStream("animatedDiffuse.png"));
//			material.setNormalTexture(texture2);
			camera = new FirstPersonCamera(coreSettings, graphicsSettings);
			scene = new SceneRenderer(renderer, RenderingStrategy.DEFERRED, coreSettings, graphicsSettings);
			//MeshComponent mesh = new MeshComponent(geometry, material, false);
			
//			Model model = ModelImporterLibrary.findImporter("obj").importModel(new AssetInputStream("crate.obj"), null);
//			model.setMaterial(material);
//			MeshComponent mesh = new MeshComponent(model, renderer, true);
			entity = new Entity();
			entity.setPosition(new Vector3f(0, 0, 0));
//			entity.addComponent(mesh);
//			scene.addEntity(entity);

			world = new World(2000, 2000, 10, 2, 2);
			
			PolyhedronBuilder builder = new PolyhedronBuilder();
			
//			Texture crateDiffuse = renderer.createTexture(new AssetInputStream("crate.png"), false);
			Material crateMaterial = new Material();
			crateMaterial.setDiffuseTexture(new AssetInputStream("crate.png"));
			Model crateModel = ModelImporterLibrary.findImporter("obj").importModel(new AssetInputStream("crate.obj"), null, renderer, (path) -> new AssetInputStream(path));
			crateModel.getMeshes().get(0).setMaterial(crateMaterial);
			Entity crate = new Entity();
			crate.setScale(new Vector3f(0.05f));
			crate.setPosition(new Vector3f(10, 0, 10));
			crate.addComponent(new MeshComponent(crateModel, renderer, false));
			crate.addComponent(new PhysicalComponent(crate.getPosition(), crate.getOrientation(), 5, new PolyhedronBounds(builder.buildCube(17))) {
				@Override
				public void onCollision(Entity other) {
					
				}
			});
			crate.getComponent(PhysicalComponent.class).setVelocity(new Vector3f(-1, 0, -1), new Vector3f(0, 0, 0));
			world.addEntity(crate);
			
			Model model = ModelImporterLibrary.findImporter("dae").importModel(new AssetInputStream("model.dae"), "Armature", renderer, (path) -> new AssetInputStream(path));
			model.getMeshes().get(0).setMaterial(material);
			Animation animation = AnimationImporterLibrary.findImporter("dae").importAnimation(new AssetInputStream("model.dae"), "Armature");
			
//			Model model = ModelImporterLibrary.findImporter("md5mesh").importModel(new AssetInputStream("md5/bob_lamp_update_export.md5mesh"), null, renderer);
//			model.getMeshes().get(0).setMaterial(material);
//			Animation animation = AnimationImporterLibrary.findImporter("md5anim").importAnimation(new AssetInputStream("md5/bob_lamp_update_export.md5anim"), null);
			
			entity.addComponent(new MeshComponent(model, renderer, false));
			entity.addComponent(new SkeletonComponent(model));
			entity.addComponent(new AnimationComponent());
			entity.getComponent(AnimationComponent.class).doAnimation(animation);
			entity.setOrientation(new Quaternionf().rotateY((float) Math.PI * 0.25f));
			entity.addComponent(new PhysicalComponent(entity.getPosition(), entity.getOrientation(), Float.MAX_VALUE, new PolyhedronBounds(builder.buildCube(4, 10, 4))) {
				@Override
				public void onCollision(Entity other) {
					System.out.println("Collision into " + other);
				}
			});
			
			world.addEntity(entity);
			
			world.addLight(new AmbientLight(1, 1, 1));
			PointLight pointLight = new PointLight(0, 0, 1);
			pointLight.setRange(20);
			pointLight.setPosition(50, 0, 50);
			world.addLight(pointLight);
//			DirectionalLight directionalLight = new DirectionalLight(1, 1, 0);
//			directionalLight.setDirection(-1, -1, -1);
//			world.addLight(directionalLight);
			spotLight = new SpotLight(0, 1, 0);
			spotLight.setRange(30);
			spotLight.setPosition(0, 25, 25);
			spotLight.setAngle((float) Math.PI / 180f);
			spotLight.setDirection(0, 1, 0);
			world.addLight(spotLight);
			scene.setLightLevel(0.3f);
			
			long seed = System.nanoTime();
			OpenSimplexNoise noise = new OpenSimplexNoise(seed);
			final float noiseResolution = 0.01f;
			
			Texture blendMap = renderer.createTexture(new AssetInputStream("blendMap.png"), false);
			Texture rTexture = renderer.createTexture(new AssetInputStream("mud.png"), true);
			Texture gTexture = renderer.createTexture(new AssetInputStream("grassFlowers.png"), true);
			Texture bTexture = renderer.createTexture(new AssetInputStream("path.png"), true);
			Texture aTexture = renderer.createTexture(new AssetInputStream("grass.png"), true);
			TerrainTexturePack pack = new TerrainTexturePack(rTexture, gTexture, bTexture, aTexture, blendMap);
			TerrainGenerator gen = (x, y) -> 15 * (float) noise.eval(x * noiseResolution, y * noiseResolution);
			world.setTerrain(0, 0, gen, pack);
			world.setTerrain(1, 0, gen, pack);
			world.setTerrain(0, 1, gen, pack);
			world.setTerrain(1, 1, gen, pack);
			
			Texture texture1_ = renderer.createCubemap(new AssetInputStream("mp_firestorm/fire-storm_rt.tga"), 
					new AssetInputStream("mp_firestorm/fire-storm_lf.tga"), new AssetInputStream("mp_firestorm/fire-storm_up.tga"), 
					new AssetInputStream("mp_firestorm/fire-storm_dn.tga"), new AssetInputStream("mp_firestorm/fire-storm_bk.tga"), 
					new AssetInputStream("mp_firestorm/fire-storm_ft.tga"));
			Texture texture2_ = renderer.createCubemap(new AssetInputStream("sky/right.png"), 
					new AssetInputStream("sky/left.png"), new AssetInputStream("sky/top.png"), 
					new AssetInputStream("sky/bottom.png"), new AssetInputStream("sky/back.png"), 
					new AssetInputStream("sky/front.png"));
			world.setSkybox(new Skybox(texture1_, texture1_, new SkyboxBlender(24, 5, 8, 21), new Vector3f(0.9f, 0.9f, 0.9f)));
			
			soundSystem = new ALSoundSystem();
			soundSystem.createContext();
			
			AudioData data = new AudioData();
			AudioStream stream = AudioDecoderLibrary.findDecoder("ogg").openStream(new AssetInputStream("01_Critical_Acclaim.ogx.ogg"), data);
			music = soundSystem.createMusic(stream, data);
			music.setLooping(true);
			music.play();
			
			particleRenderer = new ParticleRenderer(renderer, camera, new Vector2f(coreSettings.width, coreSettings.height));
			ParticleEmitter emitter0 = new BasicParticleEmitter(camera, aTexture, new Vector3f(0, 5, -10), 0.01f, 5f, 0.4f, 2, 10, 3f, 0.1f, 2f, 0.1f, new int[] { 3, 3 });
			particleRenderer.addEmitter(emitter0);
		});
		while (renderer.validContext()) {			
			float dt = renderer.getDeltaTime();
			renderer.prepareContext();
			
			t += 90 * dt;
			float dist = 10;
			
			entity.setScale(new Vector3f(1));
//			entity.setScale(new Vector3f(0.01f));
			
//			entity.setOrientation(new Quaternionf().rotationY((float) Math.toRadians(t)));
			
			Vector3f v = new Vector3f(10 * (float) Math.cos(Math.toRadians(t)), 0, 10 * (float) Math.sin(Math.toRadians(t)));
//			spotLight.setDirection(v);
			
			float height = 5;
//			cameraPosition.set(0, height, dist);
			float d = 15;
			cameraPosition.set(d, d, d);
			//cameraPosition.set(dist * (float) Math.cos(Math.toRadians(t)), dist, dist * (float) Math.sin(Math.toRadians(t)));
			camera.setPosition(cameraPosition);
			camera.setYaw(-45);
			camera.setPitch(15);
			//camera.setYaw((float) t);
//			camera.setYaw(180);
			//camera.lookAt(new Vector3f(0, height, 0));
			camera.update();
//			camera.getViewMatrix().setLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z, 0, height, 0, 0, 1, 0);
			
			world.update(dt);
			
			soundSystem.checkError();
			music.update();

			GL11.glDepthFunc(GL11.GL_LEQUAL);
			particleRenderer.update(dt, cameraPosition);
			scene.render(camera, cameraPosition, world);
			
			//GL11.glDepthFunc(GL11.GL_ALWAYS);
			//particleRenderer.render(scene.getDepthTexture());
			
//			buffer.render();
			
			renderer.updateContext();
		}
		renderer.destroyContext(() -> {
			music.delete();
			soundSystem.destroyContext();
			
//			buffer.delete();
			//cleanup
		});
	}
	
}

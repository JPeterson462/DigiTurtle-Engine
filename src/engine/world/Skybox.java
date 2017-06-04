package engine.world;

import org.joml.Vector3f;

import engine.rendering.Texture;

public class Skybox {
	
	private Texture texture1, texture2;
	
	private SkyboxBlender blender;
	
	private Vector3f fogColor;
	
	private float fogDistance = 155, fogDensity = 1;
	
	public Skybox(Texture texture1, Texture texture2, SkyboxBlender blender, Vector3f fogColor) {
		this.texture1 = texture1;
		this.texture2 = texture2;
		this.blender = blender;
		this.fogColor = fogColor;
	}
	
	public Texture getTexture1() {
		return texture1;
	}
	
	public Texture getTexture2() {
		return texture2;
	}
	
	public void update(float dt) {
		blender.update(dt);
	}
	
	public float getRotation() {
		return blender.getRotation();
	}
	
	public float getBlendFactor() {
		return blender.getBlendFactor();
	}

	public Vector3f getFogColor() {
		return fogColor;
	}

	public void setFogColor(Vector3f fogColor) {
		this.fogColor = fogColor;
	}

	public float getFogDistance() {
		return fogDistance;
	}

	public void setFogDistance(float fogDistance) {
		this.fogDistance = fogDistance;
	}

	public float getFogDensity() {
		return fogDensity;
	}

	public void setFogDensity(float fogDensity) {
		this.fogDensity = fogDensity;
	}

}

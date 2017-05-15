package engine.world;

import engine.rendering.Texture;

public class Material {
	
	private Texture diffuseTexture, normalTexture;

	private float reflectivity, shineDamper;

	public Texture getDiffuseTexture() {
		return diffuseTexture;
	}

	public void setDiffuseTexture(Texture diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}

	public Texture getNormalTexture() {
		return normalTexture;
	}

	public void setNormalTexture(Texture normalTexture) {
		this.normalTexture = normalTexture;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}
	
	public int hashCode() {
		int hashCode = 0;
		hashCode = 31 * hashCode + diffuseTexture.hashCode();
		if (normalTexture != null) {
			hashCode = 31 * hashCode + normalTexture.hashCode();
		}
		hashCode = 31 * hashCode + Float.hashCode(shineDamper);
		hashCode = 31 * hashCode + Float.hashCode(reflectivity);
		return hashCode;
	}
	
}

package library.models;

import java.io.InputStream;

public class Material {
	
	private InputStream diffuseTexture, normalTexture;

	private float reflectivity, shineDamper = 10;

	public InputStream getDiffuseTexture() {
		return diffuseTexture;
	}

	public void setDiffuseTexture(InputStream diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}

	public InputStream getNormalTexture() {
		return normalTexture;
	}

	public void setNormalTexture(InputStream normalTexture) {
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

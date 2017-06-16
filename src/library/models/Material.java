package library.models;

import java.io.InputStream;

public class Material {
	
	private InputStream diffuseTexture, normalTexture;

	private float shininess, specularFactor;

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

	public float getShininess() {
		return shininess;
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public float getSpecularFactor() {
		return specularFactor;
	}

	public void setSpecularFactor(float specularFactor) {
		this.specularFactor = specularFactor;
	}

	public int hashCode() {
		int hashCode = 0;
		hashCode = 31 * hashCode + diffuseTexture.hashCode();
		if (normalTexture != null) {
			hashCode = 31 * hashCode + normalTexture.hashCode();
		}
		hashCode = 31 * hashCode + Float.hashCode(shininess);
		hashCode = 31 * hashCode + Float.hashCode(specularFactor);
		return hashCode;
	}
	
}

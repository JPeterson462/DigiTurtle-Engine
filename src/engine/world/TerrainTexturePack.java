package engine.world;

import engine.rendering.Shader;
import engine.rendering.Texture;

public class TerrainTexturePack {

	private Texture rTexture, gTexture, bTexture, aTexture, blendMap;
	
	public TerrainTexturePack(Texture rTexture, Texture gTexture, Texture bTexture, Texture aTexture, Texture blendMap) {
		this.blendMap = blendMap;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
		this.aTexture = aTexture;
	}

	public void connect(Shader shader) {
		shader.uploadInteger(shader.getUniformLocation("blendMap"), 0);
		shader.uploadInteger(shader.getUniformLocation("rTexture"), 1);
		shader.uploadInteger(shader.getUniformLocation("gTexture"), 2);
		shader.uploadInteger(shader.getUniformLocation("bTexture"), 3);
		shader.uploadInteger(shader.getUniformLocation("aTexture"), 4);
	}
	
	public void bind() {
		blendMap.activeTexture(0);
		blendMap.bind();
		rTexture.activeTexture(1);
		rTexture.bind();
		gTexture.activeTexture(2);
		gTexture.bind();
		bTexture.activeTexture(3);
		bTexture.bind();
		aTexture.activeTexture(4);
		aTexture.bind();
	}
	
	public void unbind() {
		blendMap.unbind();
	}
	
	public void delete() {
		blendMap.delete();
		rTexture.delete();
		gTexture.delete();
		bTexture.delete();
		aTexture.delete();
	}

	public int hashCode() {
		int hashCode = 0;
		hashCode = 100 * hashCode + blendMap.getID();
		hashCode = 100 * hashCode + rTexture.getID();
		hashCode = 100 * hashCode + gTexture.getID();
		hashCode = 100 * hashCode + bTexture.getID();
		hashCode = 100 * hashCode + aTexture.getID();
		return hashCode;
	}
	
}

package engine.text;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class TextEffects {
	
	public static TextEffect newNullEffect(Vector3f textColor, float lineWidth) {
		return new TextEffect(lineWidth, 0.1f, 0, 0, textColor, new Vector3f(), new Vector2f());
	}
	
	public static TextEffect newOutlineEffect(Vector3f textColor, Vector3f outlineColor, float lineWidth, float outlineWidth) {
		return new TextEffect(lineWidth, 0.1f, outlineWidth, 0.1f, textColor, outlineColor, new Vector2f());
	}

}

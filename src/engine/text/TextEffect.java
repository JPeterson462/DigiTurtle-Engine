package engine.text;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class TextEffect {
	
	private float lineWidth, lineSharpness;
	
	private float borderWidth, borderSharpness;
	
	private Vector3f lineColor, borderColor;
	
	private Vector2f outlineOffset;

	public TextEffect(float lineWidth, float lineSharpness, float borderWidth, float borderSharpness,
			Vector3f lineColor, Vector3f borderColor, Vector2f outlineOffset) {
		this.lineWidth = lineWidth;
		this.lineSharpness = lineSharpness;
		this.borderWidth = borderWidth;
		this.borderSharpness = borderSharpness;
		this.lineColor = lineColor;
		this.borderColor = borderColor;
		this.outlineOffset = outlineOffset;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public float getLineSharpness() {
		return lineSharpness;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public float getBorderSharpness() {
		return borderSharpness;
	}

	public Vector3f getLineColor() {
		return lineColor;
	}

	public Vector3f getBorderColor() {
		return borderColor;
	}

	public Vector2f getOutlineOffset() {
		return outlineOffset;
	}

}

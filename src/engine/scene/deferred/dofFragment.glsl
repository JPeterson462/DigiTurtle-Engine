#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D depthTexture;

uniform vec2 resolution;
uniform vec2 nearFar;

uniform float focusDistance;
uniform float focusRange;

in vec2 pass_TextureCoord;

out vec4 out_Color;

// https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-effects/src/main/resources/Common/MatDefs/Post/DepthOfField.frag

void main(void) {
	vec4 texVal = texture2D(diffuseTexture, pass_TextureCoord);
	float zBuffer = texture2D(depthTexture, pass_TextureCoord).r;
	float z = (nearFar.y * nearFar.x / (nearFar.x - nearFar.y)) / (zBuffer - (nearFar.y / (nearFar.y - nearFar.x)));
	float unfocus = min(1.0, abs(z - focusDistance) / focusRange);
	if (unfocus < 0.2) {
		out_Color = texVal;
	} else {
		vec4 sum = vec4(0.0);
		float x = pass_TextureCoord.x, y = pass_TextureCoord.y;
		float xScale = 1.0 / resolution.x, yScale = 1.0 / resolution.y;
		sum += texture2D( diffuseTexture, vec2(x - 2.0 * xScale, y - 2.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x - 0.0 * xScale, y - 2.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x + 2.0 * xScale, y - 2.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x - 1.0 * xScale, y - 1.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x + 1.0 * xScale, y - 1.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x - 2.0 * xScale, y - 0.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x + 2.0 * xScale, y - 0.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x - 1.0 * xScale, y + 1.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x + 1.0 * xScale, y + 1.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x - 2.0 * xScale, y + 2.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x - 0.0 * xScale, y + 2.0 * yScale) );
		sum += texture2D( diffuseTexture, vec2(x + 2.0 * xScale, y + 2.0 * yScale) );
		sum = sum / 12.0;
		out_Color = mix(texVal, sum, unfocus);
	}
}

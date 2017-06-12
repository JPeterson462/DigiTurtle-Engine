#version 150

uniform sampler2D diffuseTexture;
uniform sampler2D depthTexture;
uniform sampler2D skyTexture;

uniform vec2 resolution;
uniform vec2 nearFar;

uniform float focusDistance;
uniform float focusRange;

uniform float fogDensity;
uniform float fogDistance;
uniform vec3 fogColor;

uniform vec4 ambientLight;

in vec2 pass_TextureCoord;

out vec4 out_Color;

const float LOG2 = 1.442695;

// 1. Depth of Field
// 2. Skybox
// 3. Fog

void main(void) {
	vec4 texVal = texture2D(diffuseTexture, pass_TextureCoord);
	float zBuffer = texture2D(depthTexture, pass_TextureCoord).r;
	float depth = (nearFar.y * nearFar.x / (nearFar.x - nearFar.y)) / (zBuffer - (nearFar.y / (nearFar.y - nearFar.x)));
	
	float unfocus = min(1.0, abs(depth - focusDistance) / focusRange);
	vec4 dofVal;
	if (unfocus < 0.2) {
		dofVal = texVal;
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
		dofVal = mix(texVal, sum, unfocus);
	}
	dofVal = texVal;
	
	vec4 skyColor = texture2D(skyTexture, pass_TextureCoord);
	
	vec4 envColor = texVal;// + skyColor;
	
	//float fogFactor = exp2(-fogDensity * fogDensity * depth * depth * LOG2);
	float fogFactor = 1.0 - depth / fogDistance;
	fogFactor = clamp(fogFactor, 1.0 - fogDensity, 1.0);
	
	envColor = mix(vec4(fogColor, 1.0), envColor, fogFactor);
	if (depth > fogDistance) {
		envColor = mix(skyColor, vec4(fogColor, 1.0), fogDensity);
	}
	
	out_Color = envColor;
}

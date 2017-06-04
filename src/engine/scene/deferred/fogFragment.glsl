#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D depthTexture;
uniform sampler2D skyTexture;

uniform vec2 nearFar;
uniform float fogDensity;
uniform float fogDistance;

in vec2 pass_TextureCoord;

out vec4 out_Color;

const float LOG2 = 1.442695;

// https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-effects/src/main/resources/Common/MatDefs/Post/Fog.frag

void main(void) {
	vec4 fogColor = texture2D(skyTexture, pass_TextureCoord);
	float near = 1.0;
	float far = fogDistance;
	vec4 texVal = texture2D(diffuseTexture, pass_TextureCoord);
	float fogVal = texture2D(depthTexture, pass_TextureCoord).r;
	float depth = (2.0 * nearFar.x) / (nearFar.y + nearFar.x - fogVal * (nearFar.y - nearFar.x));
	float fogFactor = exp2(-fogDensity * fogDensity * depth * depth * LOG2);
	fogFactor = clamp(fogFactor, 0.0, 1.0);
	fogFactor = max(0.95, fogFactor);
	out_Color = mix(fogColor, texVal, fogFactor);
}

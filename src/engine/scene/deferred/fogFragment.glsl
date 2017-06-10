#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D depthTexture;
uniform sampler2D skyTexture;

uniform vec2 nearFar;
uniform float fogDensity;
uniform float fogDistance;
uniform vec3 fogColor;

in vec2 pass_TextureCoord;

out vec4 out_Color;

const float LOG2 = 1.442695;

// https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-effects/src/main/resources/Common/MatDefs/Post/Fog.frag
// https://stackoverflow.com/questions/21549456/how-to-implement-a-ground-fog-glsl-shader

void main(void) {
	vec4 rFogColor = vec4(fogColor, 1.0);
	float near = 1.0;
	float far = fogDistance;
	vec4 texVal = texture2D(diffuseTexture, pass_TextureCoord);
	float zBuffer = texture2D(depthTexture, pass_TextureCoord).r;
	float depth = (nearFar.y * nearFar.x / (nearFar.x - nearFar.y)) / (zBuffer - (nearFar.y / (nearFar.y - nearFar.x)));
	float fogFactor = 1.0 - exp2(-fogDensity * fogDensity * depth * depth * LOG2);
	//float x = ((depth - near) / (far - near));
	//float fogFactor = 1.0 - x;
	fogFactor = clamp(fogFactor, 1.0 - fogDensity, 1.0);
	vec4 foreground = mix(rFogColor, texVal, fogFactor);
	vec4 background = texture2D(skyTexture, pass_TextureCoord);
	out_Color = foreground + background * (1.0 - foreground.a);
	out_Color.a = texVal.a;
	if (depth > far) {
		out_Color = mix(background, rFogColor, fogDensity);
		out_Color.a = background.a;
	}
}

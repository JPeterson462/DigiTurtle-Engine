#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D depthTexture;
uniform sampler2D skyTexture;

uniform mat4 invViewMatrix;
uniform mat4 invProjectionMatrix;
uniform vec3 cameraPosition;

uniform vec2 nearFar;
uniform float fogDensity;
uniform float fogDistance;
uniform vec3 fogColor;

in vec2 pass_TextureCoord;

out vec4 out_Color;

const float LOG2 = 1.442695;

// https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-effects/src/main/resources/Common/MatDefs/Post/Fog.frag
// https://stackoverflow.com/questions/21549456/how-to-implement-a-ground-fog-glsl-shader

vec3 reconstructPosition() {
	vec4 clipSpaceLocation;
	clipSpaceLocation.xy = pass_TextureCoord * 2.0 - 1.0;
	clipSpaceLocation.z = texture2D(depthTexture, pass_TextureCoord).r * 2.0 - 1.0;
	clipSpaceLocation.w = 1.0;
	vec4 homogenousLocation = invViewMatrix * invProjectionMatrix * clipSpaceLocation;
	return homogenousLocation.xyz / homogenousLocation.w;
}

void main(void) {
	vec4 rFogColor = vec4(fogColor, 1.0);
	float near = 1.0;
	float far = fogDistance;
	vec4 texVal = texture2D(diffuseTexture, pass_TextureCoord);
	vec3 position = reconstructPosition();
	vec3 cameraToPoint = position - cameraPosition;
	float depth = length(cameraToPoint);
	//float fogFactor = exp2(-fogDensity * fogDensity * depth * depth * LOG2);
	float x = ((depth - near) / (far - near));
	float fogFactor = 1.0 - x * x;
	fogFactor = clamp(fogFactor, 1.0 - fogDensity, 1.0);
	vec4 foreground = mix(rFogColor, texVal, fogFactor);
	vec4 background = texture2D(skyTexture, pass_TextureCoord);
	out_Color = foreground + background * (1.0 - foreground.a);
	if (depth > far) {
		out_Color = background * (1.0 - fogDensity) + rFogColor * fogDensity;
	}
}

#version 330

uniform samplerCube cubeMap1;
uniform samplerCube cubeMap2;

uniform float blendFactor;

in vec3 pass_TexCoord;

out vec4 out_Color;

const float lowerLimit = 0.0;
const float upperLimit = 30.0;

void main(void) {
	vec4 color1 = textureCube(cubeMap1, pass_TexCoord);
	vec4 color2 = textureCube(cubeMap2, pass_TexCoord);
	vec4 finalColor = mix(color1, color2, blendFactor);
	out_Color = finalColor;
}

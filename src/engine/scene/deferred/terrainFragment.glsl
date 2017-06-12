#version 330

uniform sampler2D blendMap;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D aTexture;

in vec2 pass_TextureCoord;
in vec3 pass_Normal;

out vec4 out_Color;

void main(void) {
	vec4 blendSample = texture2D(blendMap, pass_TextureCoord);
//	vec2 terrainTextureCoord = pass_TextureCoord * 75.0;
	vec2 terrainTextureCoord = pass_TextureCoord;
	vec4 rSample = texture2D(rTexture, terrainTextureCoord) * blendSample.r;
	vec4 gSample = texture2D(gTexture, terrainTextureCoord) * blendSample.g;
	vec4 bSample = texture2D(bTexture, terrainTextureCoord) * blendSample.b;
	vec4 aSample = texture2D(aTexture, terrainTextureCoord) * (1.0 - (blendSample.r + blendSample.g + blendSample.b));
	out_Color = rSample + gSample + bSample + aSample;
}

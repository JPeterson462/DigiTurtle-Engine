#version 330

uniform sampler2D blendMap;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D aTexture;

in vec2 pass_TextureCoord;
in vec2 pass_BlendCoord;
in vec3 pass_Normal;

out vec4 out_Color0;
out vec4 out_Color1;

void main(void) {
	vec4 blendSample = texture2D(blendMap, pass_BlendCoord);
	vec2 terrainTextureCoord = pass_TextureCoord;
	vec4 rSample = texture2D(rTexture, terrainTextureCoord) * blendSample.r;
	vec4 gSample = texture2D(gTexture, terrainTextureCoord) * blendSample.g;
	vec4 bSample = texture2D(bTexture, terrainTextureCoord) * blendSample.b;
	vec4 aSample = texture2D(aTexture, terrainTextureCoord) * (1.0 - (blendSample.r + blendSample.g + blendSample.b));
	out_Color0 = rSample + gSample + bSample + aSample;
	out_Color1 = vec4(normalize(pass_Normal) * 0.5 + 0.5, 1.0);
}

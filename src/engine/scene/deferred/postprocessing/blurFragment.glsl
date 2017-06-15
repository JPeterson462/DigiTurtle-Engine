#version 330

uniform sampler2D diffuseTexture;

uniform vec2 resolution;
uniform int horizontal;

in vec2 pass_TextureCoord;

out vec4 out_Color;

// https://learnopengl.com/#!Advanced-Lighting/Bloom

const float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

vec3 horizontalBlur(vec2 texelSize) {
	vec3 blur = vec3(0.0);
	blur += texture2D(diffuseTexture, pass_TextureCoord + vec2(texelSize.x * 1, 0.0)).rgb * weight[1];
	blur += texture2D(diffuseTexture, pass_TextureCoord - vec2(texelSize.x * 1, 0.0)).rgb * weight[1];
	blur += texture2D(diffuseTexture, pass_TextureCoord + vec2(texelSize.x * 2, 0.0)).rgb * weight[2];
	blur += texture2D(diffuseTexture, pass_TextureCoord - vec2(texelSize.x * 2, 0.0)).rgb * weight[2];
	blur += texture2D(diffuseTexture, pass_TextureCoord + vec2(texelSize.x * 3, 0.0)).rgb * weight[3];
	blur += texture2D(diffuseTexture, pass_TextureCoord - vec2(texelSize.x * 3, 0.0)).rgb * weight[3];
//	blur += texture2D(diffuseTexture, pass_TextureCoord + vec2(texelSize.x * 4, 0.0)).rgb * weight[4];
//	blur += texture2D(diffuseTexture, pass_TextureCoord - vec2(texelSize.x * 4, 0.0)).rgb * weight[4];
	return blur;
}

vec3 verticalBlur(vec2 texelSize) {
	vec3 blur = vec3(0.0);
	blur += texture2D(diffuseTexture, pass_TextureCoord + vec2(0.0, texelSize.y * 1)).rgb * weight[1];
	blur += texture2D(diffuseTexture, pass_TextureCoord - vec2(0.0, texelSize.y * 1)).rgb * weight[1];
	blur += texture2D(diffuseTexture, pass_TextureCoord + vec2(0.0, texelSize.y * 2)).rgb * weight[2];
	blur += texture2D(diffuseTexture, pass_TextureCoord - vec2(0.0, texelSize.y * 2)).rgb * weight[2];
	blur += texture2D(diffuseTexture, pass_TextureCoord + vec2(0.0, texelSize.y * 3)).rgb * weight[3];
	blur += texture2D(diffuseTexture, pass_TextureCoord - vec2(0.0, texelSize.y * 3)).rgb * weight[3];
//	blur += texture2D(diffuseTexture, pass_TextureCoord + vec2(0.0, texelSize.y * 4)).rgb * weight[4];
//	blur += texture2D(diffuseTexture, pass_TextureCoord - vec2(0.0, texelSize.y * 4)).rgb * weight[4];
	return blur;
}

void main(void) {
	vec2 texelSize = vec2(1.0 / resolution.x, 1.0 / resolution.y);
	vec4 pixel = texture2D(diffuseTexture, pass_TextureCoord);
	vec3 blur = pixel.rgb * weight[0];
	if (horizontal > 0) {
		blur += horizontalBlur(texelSize);
	} else {
		blur += verticalBlur(texelSize);
	}
	out_Color = vec4(blur, pixel.a);
}

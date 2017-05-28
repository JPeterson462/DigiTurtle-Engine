#version 330

uniform mat4 projectionMatrix;

in vec2 in_Position;

in mat4 modelViewMatrix;
in vec4 textureAtlasOffset;
in float blendFactor;

uniform vec2 textureAtlasSize;

out vec2 pass_TexCoord0;
out vec2 pass_TexCoord1;
out float pass_Blend;

void main(void) {
	gl_Position = projectionMatrix * modelViewMatrix * vec4(in_Position, 0.0, 1.0);
	vec2 texCoord = in_Position + vec2(0.5, 0.5);
	texCoord.y = 1.0 - texCoord.y;
	texCoord /= textureAtlasSize;
	pass_TexCoord0 = texCoord + textureAtlasOffset.xy;
	pass_TexCoord1 = texCoord + textureAtlasOffset.zw;
	pass_Blend = blendFactor;
}

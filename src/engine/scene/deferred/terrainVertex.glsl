#version 330

uniform mat4 mvpMatrix;

in vec3 in_Position;
in vec2 in_TextureCoord;
in vec3 in_Normal;

out vec2 pass_TextureCoord;
out vec2 pass_BlendCoord;
out vec3 pass_Normal;

const float TERRAIN_SCALE = 75.0;

void main(void) {
	gl_Position = mvpMatrix * vec4(in_Position, 1.0);
	pass_BlendCoord = in_TextureCoord;
	pass_TextureCoord = vec2(in_TextureCoord.x * TERRAIN_SCALE, in_TextureCoord.y * TERRAIN_SCALE);
	pass_Normal = in_Normal;
}

#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec3 lightPosition;

in vec2 in_Position;
in vec2 in_TextureCoord;

out vec3 pass_LightPos;
out vec2 pass_TextureCoord;
out mat3 pass_NormalMatrix;

void main(void) {
	gl_Position = vec4(in_Position, 0.0, 1.0);
	pass_LightPos = vec3(viewMatrix * vec4(lightPosition, 1.0));
	pass_TextureCoord = in_TextureCoord;
	pass_NormalMatrix = mat3(transpose(inverse(viewMatrix * modelMatrix)));
}

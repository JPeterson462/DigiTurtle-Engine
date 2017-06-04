#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec3 in_Position;

out vec3 pass_TexCoord;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(in_Position, 1.0);
	//gl_Position.z = gl_Position.z * 0.9999;
	pass_TexCoord = in_Position;
}

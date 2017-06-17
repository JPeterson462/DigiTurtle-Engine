#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec3 in_Position;

void main(void) {
	vec4 positionRelativeToCamera = viewMatrix * (modelMatrix * vec4(in_Position, 1.0));
	gl_Position = projectionMatrix * positionRelativeToCamera;
}

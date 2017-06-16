#version 150

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec3 in_Position;
in vec2 in_TextureCoord;
in vec3 in_Normal;

out vec2 pass_TextureCoord;
out vec3 pass_Normal;
out vec3 pass_ViewSpacePos;
out mat3 pass_NormalMatrix;

void main(void) {
	vec4 positionRelativeToCamera = viewMatrix * (modelMatrix * vec4(in_Position, 1.0));
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_ViewSpacePos = positionRelativeToCamera.xyz;
	pass_TextureCoord = in_TextureCoord;
	pass_Normal = in_Normal;
	pass_NormalMatrix = mat3(transpose(inverse(viewMatrix * modelMatrix)));
}

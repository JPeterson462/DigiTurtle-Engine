#version 150

const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 jointTransforms[MAX_JOINTS];

in vec3 in_Position;
in vec2 in_TextureCoord;
in vec3 in_Normal;
in vec3 in_Joints;
in vec3 in_Weights;

out vec2 pass_TextureCoord;
out vec3 pass_Normal;
out vec3 pass_ViewSpacePos;
out mat3 pass_NormalMatrix;

void main(void) {
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);

	for(int i = 0; i < MAX_WEIGHTS; i++){
		mat4 jointTransform = jointTransforms[int(in_Joints[i])];
		vec4 posePosition = jointTransform * vec4(in_Position, 1.0);
		totalLocalPos += posePosition * in_Weights[i];

		vec4 worldNormal = jointTransform * vec4(in_Normal, 0.0);
		totalNormal += worldNormal * in_Weights[i];
	}

	vec4 worldPosition = modelMatrix * totalLocalPos;
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	pass_ViewSpacePos = positionRelativeToCamera.xyz;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_TextureCoord = in_TextureCoord;
	pass_Normal = totalNormal.xyz;
	pass_NormalMatrix = mat3(transpose(inverse(viewMatrix * modelMatrix)));
}

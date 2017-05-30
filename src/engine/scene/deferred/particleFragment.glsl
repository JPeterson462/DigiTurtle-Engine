#version 330

uniform sampler2D texture;
uniform sampler2D sceneDepthTexture;
uniform vec2 windowSize;

in vec2 pass_TexCoord0;
in vec2 pass_TexCoord1;
in float pass_Blend;

out vec4 out_Color;

void main(void) {
	float fragDepth = gl_FragCoord.z;
	float sceneDepth = texture2D(sceneDepthTexture, gl_FragCoord.xy / windowSize).r;
	if (fragDepth > sceneDepth) {
		discard;
	}
	vec4 color0 = texture2D(texture, pass_TexCoord0);
	vec4 color1 = texture2D(texture, pass_TexCoord1);
	out_Color = mix(color0, color1, pass_Blend);
}

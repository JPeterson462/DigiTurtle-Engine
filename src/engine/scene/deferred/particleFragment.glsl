#version 330

uniform sampler2D texture;

in vec2 pass_TexCoord0;
in vec2 pass_TexCoord1;
in float pass_Blend;

out vec4 out_Color;

void main(void) {
	vec4 color0 = texture2D(texture, pass_TexCoord0);
	vec4 color1 = texture2D(texture, pass_TexCoord1);
	out_Color = mix(color0, color1, pass_Blend);
}

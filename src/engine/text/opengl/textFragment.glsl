#version 330

uniform sampler2D fontPage;

uniform vec3 textColor;
uniform float textWidth;
uniform float textEdge;

uniform vec2 outlineOffset;
uniform vec3 outlineColor;
uniform float outlineWidth;
uniform float outlineEdge;

in vec2 pass_TexCoord;

out vec4 out_Color;

void main(void) {
	float distanceText = 1.0 - texture2D(fontPage, pass_TexCoord).a;
	float alphaText = 1.0 - smoothstep(textWidth, textWidth + textEdge, distanceText);
	float distanceOutline = 1.0 - texture2D(fontPage, pass_TexCoord + outlineOffset).a;
	float alphaOutline = 1.0 - smoothstep(outlineWidth, outlineWidth + outlineEdge, distanceOutline);

	float alpha = alphaText + (1.0 - alphaText) * alphaOutline;
	vec3 color = mix(outlineColor, textColor, alphaText / alpha);
	out_Color = vec4(color, alpha);
}

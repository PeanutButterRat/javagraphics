#version 430

in vec2 tc;
out vec4 color;

uniform mat4 m;
uniform mat4 v;
uniform mat4 p;
layout (binding = 0) uniform sampler2D sampler;

void main(void) {
    color = texture(sampler, tc);
}
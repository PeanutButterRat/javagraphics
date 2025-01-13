#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texture;
out vec2 tc;

uniform mat4 m;
uniform mat4 v;
uniform mat4 p;

void main(void) {
    gl_Position = p * v * m * vec4(position, 1.0);
    tc = texture;
}
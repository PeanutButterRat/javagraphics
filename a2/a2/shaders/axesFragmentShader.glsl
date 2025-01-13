#version 430

in vec4 vcolor;
out vec4 color;

uniform mat4 m;
uniform mat4 v;
uniform mat4 p;

void main(void) {
    color = vcolor;
}
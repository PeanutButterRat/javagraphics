#version 430

in vec4 vcolor;
out vec4 color;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;

void main(void) {
    color = vcolor;
}
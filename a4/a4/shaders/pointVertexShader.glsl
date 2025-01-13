#version 430

uniform vec3 position;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;

void main(void) {
    gl_Position = p_matrix * v_matrix * m_matrix * vec4(position, 1.0);
}
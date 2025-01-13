#version 430

out vec4 vcolor;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;

void main(void) {
    vec4 position;

    if (gl_VertexID == 0) {
        position = vec4(0.0, 0.0, 0.0, 1.0);
        vcolor = vec4(1.0, 0.0, 0.0, 1.0);
    } else if (gl_VertexID == 1) {
        position = vec4(1.0, 0.0, 0.0, 1.0);
        vcolor = vec4(1.0, 0.0, 0.0, 1.0);
    } else if (gl_VertexID == 2) {
        position = vec4(0.0, 0.0, 0.0, 1.0);
        vcolor = vec4(0.0, 1.0, 0.0, 1.0);
    } else if (gl_VertexID == 3) {
        position = vec4(0.0, 1.0, 0.0, 1.0);
        vcolor = vec4(0.0, 1.0, 0.0, 1.0);
    } else if (gl_VertexID == 4) {
        position = vec4(0.0, 0.0, 0.0, 1.0);
        vcolor = vec4(0.0, 0.0, 1.0, 1.0);
    } else {
        position = vec4(0.0, 0.0, 1.0, 1.0);
        vcolor = vec4(0.0, 0.0, 1.0, 1.0);
    }
    gl_Position = p_matrix * v_matrix * m_matrix * position;
}
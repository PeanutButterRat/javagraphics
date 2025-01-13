package a3;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

public class Plane {
    private final GL4 gl;
    private final int[] vbo = new int[1];
    private final int totalVertices;


    public Plane(GL4 gl, int subdivisions) {
        this.gl = gl;
        gl.glGenBuffers(vbo.length, vbo, 0);

        int rows = subdivisions + 1;
        int quads = rows * rows;
        int triangles = quads * 2;
        int triangleVertices = triangles * 3;
        totalVertices = triangleVertices * 3;
        float[] vertices = new float[totalVertices * 3];

        int i = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < rows; x++) {
                vertices[i++] = 1f / rows * x - 0.5f;
                vertices[i++] = 0;
                vertices[i++] = 1f / rows * y - 0.5f;

                vertices[i++] = 1f / rows * (x + 1) - 0.5f;
                vertices[i++] = 0;
                vertices[i++] = 1f / rows * (y + 1) - 0.5f;

                vertices[i++] = 1f / rows * (x + 1) - 0.5f;
                vertices[i++] = 0;
                vertices[i++] = 1f / rows * y - 0.5f;

                vertices[i++] = 1f / rows * x - 0.5f;
                vertices[i++] = 0;
                vertices[i++] = 1f / rows * y - 0.5f;

                vertices[i++] = 1f / rows * x - 0.5f;
                vertices[i++] = 0;
                vertices[i++] = 1f / rows * (y + 1) - 0.5f;

                vertices[i++] = 1f / rows * (x + 1) - 0.5f;
                vertices[i++] = 0;
                vertices[i++] = 1f / rows * (y + 1) - 0.5f;
            }
        }

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer buffer = Buffers.newDirectFloatBuffer(vertices);
        gl.glBufferData(GL_ARRAY_BUFFER, buffer.limit() * 4L, buffer, GL_STATIC_DRAW);
    }

    public void draw() {
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        // Vertices.
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glDrawArrays(GL_TRIANGLES, 0, totalVertices);
    }
}

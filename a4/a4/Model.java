package a4;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.*;

public class Model {
    private final int vertexCount;
    private final int[] vbo = new int[3];
    private int texture;
    private final GL4 gl;

    public Model(GL4 gl, String modelFilepath, String textureFilepath) {
        this.gl = gl;
        gl.glGenBuffers(vbo.length, vbo, 0);

        ImportedModel model = new ImportedModel(modelFilepath);
        texture = Utils.loadTexture(textureFilepath);
        float[][] modelData = unpackVertices(model);
        vertexCount = modelData[0].length / 3;

        bindBufferData(gl, vbo[0], modelData[0]);
        bindBufferData(gl, vbo[1], modelData[1]);
        bindBufferData(gl, vbo[2], modelData[2]);
    }

    private void bindBufferData(GL4 gl, int vbo, float[] data) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Buffers.newDirectFloatBuffer(data);
        gl.glBufferData(GL_ARRAY_BUFFER, buffer.limit() * 4L, buffer, GL_STATIC_DRAW);
    }

    public void draw() {
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        // Vertices.
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        // Texture coordinates.
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        // Normals.
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(2);

        // Texture.
        gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_2D, texture);

        gl.glDrawArrays(GL_TRIANGLES, 0, vertexCount);
    }

    public float[][] unpackVertices(ImportedModel model) {
        int vertexCount = model.getNumVertices();
        Vector3f[] vertices = model.getVertices();
        Vector2f[] textures = model.getTexCoords();
        Vector3f[] normals = model.getNormals();

        float[] vValues = new float[vertexCount * 3];
        float[] tValues = new float[vertexCount * 2];
        float[] nValues = new float[vertexCount * 3];

        for (int i = 0; i < vertexCount; i++) {
            vValues[i * 3] = vertices[i].x;
            vValues[i * 3 + 1] = vertices[i].y;
            vValues[i * 3 + 2] = vertices[i].z;

            tValues[i * 2] = textures[i].x;
            tValues[i * 2 + 1] = textures[i].y;

            nValues[i * 3] = normals[i].x;
            nValues[i * 3 + 1] = normals[i].y;
            nValues[i * 3 + 2] = normals[i].z;
        }

        return new float[][]{ vValues, tValues, nValues };
    }

    void setTexture(int texture) {
        this.texture = texture;
    }
}

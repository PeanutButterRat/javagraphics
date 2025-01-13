package a2;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.*;

public class Model {
    private int vertexCount;
    private int[] vao = new int[1];
    private int[] vbo = new int[2];

    private int texture;
    private int textureUnit;
    private GL4 gl;

    private static int textureCount = 0;


    public Model(GL4 gl, String modelFilepath, String textureFilepath) {
        this.gl = gl;
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        ImportedModel model = new ImportedModel(modelFilepath);
        texture = Utils.loadTexture(textureFilepath);
        textureUnit = getTextureUnit();
        float[][] modelData = unpackVertices(model);
        vertexCount = modelData[0].length / 3;

        bindBufferData(gl, vbo[0], modelData[0]);
        bindBufferData(gl, vbo[1], modelData[1]);
    }

    public Model(GL4 gl, float[] vertices, float[] textureCoordinates, String textureFilepath) {
        this.gl = gl;
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        texture = Utils.loadTexture(textureFilepath);
        textureUnit = getTextureUnit();
        vertexCount = vertices.length / 3;

        bindBufferData(gl, vbo[0], vertices);
        bindBufferData(gl, vbo[1], textureCoordinates);
    }

    private void bindBufferData(GL4 gl, int vbo, float[] data) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Buffers.newDirectFloatBuffer(data);
        gl.glBufferData(GL_ARRAY_BUFFER, buffer.limit() * 4L, buffer, GL_STATIC_DRAW);
    }

    public void draw() {
        // Vertices.
        gl.glBindVertexArray(vao[0]);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        // Texture coordinates.
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(textureUnit);
        gl.glBindTexture(GL_TEXTURE_2D, texture);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
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

    private int getTextureUnit() {
        switch (textureUnit++) {
            case 0:
                return GL_TEXTURE0;
            case 1:
                return GL_TEXTURE1;
            case 2:
                return GL_TEXTURE2;
            case 3:
                return GL_TEXTURE3;
            case 4:
                return GL_TEXTURE4;
            case 5:
                return GL_TEXTURE5;
            case 6:
                return GL_TEXTURE6;
            case 7:
                return GL_TEXTURE7;
            case 8:
                return GL_TEXTURE8;
            default:
                throw new RuntimeException("Ran out of texture units");
        }
    }
}

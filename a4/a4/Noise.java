package a4;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Random;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_3D;
import static com.jogamp.opengl.GL2GL3.GL_UNSIGNED_INT_8_8_8_8_REV;

public class Noise {
    private static final int NOISE_WIDTH = 256;
    private static final int NOISE_HEIGHT = 256;
    private static Random random = new Random();
    private static byte[] data = new byte[NOISE_WIDTH * NOISE_HEIGHT * 4];


    public static int generateNoiseTexture(GL4 gl) {
        for (int x = 0; x < NOISE_WIDTH; x++) {
            for (int y = 0; y < NOISE_HEIGHT; y++) {
                double noise = random.nextDouble();
                int rgb = (int) (255 * noise);
                data[y * (NOISE_WIDTH * 4) + x * 4] = (byte) rgb;
                data[y * (NOISE_WIDTH * 4) + x * 4 + 1] = (byte) rgb;
                data[y * (NOISE_WIDTH * 4) + x * 4 + 2] = (byte) rgb;
                data[y * (NOISE_WIDTH * 4) + x * 4 + 3] = (byte) 1;
            }
        }

        ByteBuffer buffer = Buffers.newDirectByteBuffer(data);

        int[] texture = new int[1];
        gl.glGenTextures(1, texture, 0);
        gl.glBindTexture(GL_TEXTURE_2D, texture[0]);
        gl.glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, NOISE_WIDTH, NOISE_HEIGHT);
        gl.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, NOISE_WIDTH, NOISE_HEIGHT, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        gl.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        return texture[0];
    }
}

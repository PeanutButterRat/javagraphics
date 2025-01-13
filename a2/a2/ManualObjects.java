package a2;

public class ManualObjects {
    public static final float[] CASTLE_VERTEX_POSITIONS = {
            // Bottom.
            -1, 0, 1,
            1, 0, 1,
            1, 0, -1,

            1, 0, -1,
            -1, 0, -1,
            -1, 0, 1,

            // Right.
            1, 0, -1,
            1, 2, -1,
            1, 2, 1,

            1, 0, -1,
            1, 2, 1,
            1, 0, 1,

            // Left.
            -1, 0, -1,
            -1, 2, -1,
            -1, 2, 1,

            -1, 0, -1,
            -1, 2, 1,
            -1, 0, 1,

            // Front.
            -1, 0, 1,
            -1, 2, 1,
            1, 2, 1,

            -1, 0, 1,
            1, 2, 1,
            1, 0, 1,

            // Back.
            -1, 0, -1,
            -1, 2, -1,
            1, 2, -1,

            -1, 0, -1,
            1, 2, -1,
            1, 0, -1,

            // Front-top.
            -1, 2, -1,
            0, 8, 0,
            1, 2, -1,

            // Back-top.
            -1, 2, 1,
            0, 8, 0,
            1, 2, 1,

            // Left-top.
            -1, 2, -1,
            0, 8, 0,
            -1, 2, 1,

            // Right-top.
            1, 2, -1,
            0, 8, 0,
            1, 2, 1,
    };

    public static final float[] CASTLE_TEXTURE_COORDINATES = {
            0, 1,
            1, 1,
            1, 0,
            1, 0,
            0, 0,
            0, 1,

            0, 0,
            0, 1,
            1, 1,
            0, 0,
            1, 1,
            1, 0,

            0, 0,
            0, 1,
            1, 1,
            0, 0,
            1, 1,
            1, 0,

            0, 0,
            0, 1,
            1, 1,
            0, 0,
            1, 1,
            1, 0,

            0, 0,
            0, 1,
            1, 1,
            0, 0,
            1, 1,
            1, 0,

            0, 0,
            0.5f, 4,
            1, 0,

            0, 0,
            0.5f, 4,
            1, 0,

            0, 0,
            0.5f, 4,
            1, 0,

            0, 0,
            0.5f, 4,
            1, 0
    };

    public static final float[] BUSH_VERTICES = {
            1f, 0f, 1f, 2f, 1f, 1f, 2f, 1f, -1f,
            2f, 1f, -1f, 1f, 0f, -1f, 1f, 0f, 1f,

            2f, 1f, 1f, 1f, 2f, 1f, 1f, 2f, -1f,
            1f, 2f, -1f, 2, 1, -1, 2f, 1f, 1f,

            1f, 2f, 1f, 1f, 2f, -1f, -1f, 2f, -1f,
            -1f, 2f, -1f, -1f, 2f, 1f, 1f, 2f, 1f,

            -1f, 2f, -1f, -2f, 1f, -1f, -2f, 1f, 1f,
            -2f, 1f, 1f, -1f, 2f, 1f, -1f, 2f, -1f,

            -1f, 0f, 1f, -2f, 1f, 1f, -2f, 1f, -1f,
            -2f, 1f, -1f, -1f, 0f, -1f, -1f, 0f, 1f,

            1f, 0f, 1f, 1f, 0f, -1f, -1f, 0f, -1f,
            -1f, 0f, -1f, -1f, 0f, 1f, 1f, 0f, 1f,

            1f, 0f, 1f, 1f, 2f, 1f, 2f, 1f, 1f,
            -1f, 0f, 1f, -1f, 2f, 1f, -2f, 1f, 1f,
            -1f, 0f, 1f, 1f, 0f, 1f, -1f, 2f, 1f,
            -1f, 2f, 1f, 1f, 2f, 1f, 1f, 0f, 1f,

            1f, 0f, -1f, 1f, 2f, -1f, 2f, 1f, -1f,
            -1f, 0f, -1f, -1f, 2f, -1f, -2f, 1f, -1f,
            -1f, 0f, -1f, 1f, 0f, -1f, -1f, 2f, -1f,
            -1f, 2f, -1f, 1f, 2f, -1f, 1f, 0f, -1f,
    };

    public static final float[] BUSH_TEXTURE_COORDINATES = new float[] {
            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,

            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,

            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,

            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,

            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,

            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,

            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,
            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,

            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,
            0f, 1f, 1f, 1f, 0f, 1f,
            0f, 1f, 0f, 0f, 0f, 1f,
    };
}

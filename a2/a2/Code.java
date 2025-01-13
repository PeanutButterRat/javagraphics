package a2;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.*;

import org.joml.Matrix4f;

import java.awt.event.*;
import java.nio.FloatBuffer;
import javax.swing.*;


public class Code extends JFrame implements GLEventListener, MouseWheelListener, KeyListener {
    private long lastFrame;
    private float deltaTime;
    private int modelRenderingProgram;
    private int axesRenderingProgram;
    private FloatBuffer buffer = Buffers.newDirectFloatBuffer(16);

    private GLCanvas canvas;
    private GL4 gl;

    private boolean axesVisible = true;

    private Camera camera = new Camera();

    private final float FOV = (float) Math.toRadians(60);
    private final float NEAR_CLIPPING = 0.1f;
    private final float FAR_CLIPPING = 1000f;

    private final float MOVEMENT_INCREMENT = 2f;
    private final float ROTATION_INCREMENT = 0.1f;

    private final float PLANE_RADIUS = 30f;
    private final float PLANE_SPEED = 50f;
    private float planeRotationDegrees = 0;

    private Model plane1;
    private Model plane2;
    private Model ground;
    private Model castle;
    private Model bush;


    Matrix4f perspective = new Matrix4f().identity();
    Matrix4f modelMatrix = new Matrix4f().identity();

    public Code() {
        setTitle("Assignment #2 - 3D Models and Viewing by Eric Brown");
        setSize(900, 600);
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        this.add(canvas);
        this.setVisible(true);

        canvas.addKeyListener(this);
        this.addMouseWheelListener(this);

        Animator animator = new Animator(canvas);
        animator.start();

        lastFrame = System.currentTimeMillis();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = (GL4) GLContext.getCurrentGL();
        modelRenderingProgram = Utils.createShaderProgram("a2/shaders/modelVertexShader.glsl", "a2/shaders/modelFragmentShader.glsl");
        axesRenderingProgram = Utils.createShaderProgram("a2/shaders/axesVertexShader.glsl", "a2/shaders/axesFragmentShader.glsl");
        setupVertices();

        camera.moveAlongNAxis(-50);
        camera.moveAlongVAxis(25);
        camera.pitch(-0.2f);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        long currentFrame = System.currentTimeMillis();
        deltaTime = (currentFrame - lastFrame) / 1000f;
        lastFrame = currentFrame;

        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glUseProgram(modelRenderingProgram);

        // Perspective matrix.
        float aspectRatio = (float) canvas.getWidth() / canvas.getHeight();
        perspective.setPerspective(FOV, aspectRatio, NEAR_CLIPPING, FAR_CLIPPING);
        int pLocation = gl.glGetUniformLocation(modelRenderingProgram, "p");
        gl.glUniformMatrix4fv(pLocation, 1, false, perspective.get(buffer));

        // View matrix.
        int vLocation = gl.glGetUniformLocation(modelRenderingProgram, "v");
        gl.glUniformMatrix4fv(vLocation, 1, false, camera.getViewMatrix().get(buffer));

        // Drawing Plane 1 (orange).
        planeRotationDegrees = (planeRotationDegrees + PLANE_SPEED * deltaTime) % 360;
        modelMatrix.identity();
        modelMatrix.rotateY((float) Math.toRadians(planeRotationDegrees));
        modelMatrix.translate(PLANE_RADIUS, 10, 0);
        modelMatrix.scale(0.25f);
        int mLocation = gl.glGetUniformLocation(modelRenderingProgram, "m");
        gl.glUniformMatrix4fv(mLocation, 1, false, modelMatrix.get(buffer));
        plane1.draw();

        // Drawing Plane 2 (white).
        modelMatrix.identity();
        modelMatrix.rotateY((float) Math.toRadians(planeRotationDegrees + 180));
        modelMatrix.translate(PLANE_RADIUS, -10, 0);
        modelMatrix.rotateY((float) Math.toRadians(90));
        modelMatrix.scale(2);
        mLocation = gl.glGetUniformLocation(modelRenderingProgram, "m");
        gl.glUniformMatrix4fv(mLocation, 1, false, modelMatrix.get(buffer));
        plane2.draw();

        // Drawing the ground.
        modelMatrix.identity();
        modelMatrix.scale(10);
        mLocation = gl.glGetUniformLocation(modelRenderingProgram, "m");
        gl.glUniformMatrix4fv(mLocation, 1, false, modelMatrix.get(buffer));
        ground.draw();

        // Drawing the castle.
        modelMatrix.identity();
        modelMatrix.scale(5);
        mLocation = gl.glGetUniformLocation(modelRenderingProgram, "m");
        gl.glUniformMatrix4fv(mLocation, 1, false, modelMatrix.get(buffer));
        castle.draw();

        // Drawing the bush.
        modelMatrix.identity();
        modelMatrix.scale(2);
        modelMatrix.translate(-10, 0, -10);
        mLocation = gl.glGetUniformLocation(modelRenderingProgram, "m");
        gl.glUniformMatrix4fv(mLocation, 1, false, modelMatrix.get(buffer));
        bush.draw();

        if (axesVisible) drawWorldAxes();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_W:
                camera.moveAlongNAxis(MOVEMENT_INCREMENT);
                break;
            case KeyEvent.VK_S:
                camera.moveAlongNAxis(-MOVEMENT_INCREMENT);
                break;
            case KeyEvent.VK_A:
                camera.moveAlongUAxis(-MOVEMENT_INCREMENT);
                break;
            case KeyEvent.VK_D:
                camera.moveAlongUAxis(MOVEMENT_INCREMENT);
                break;
            case KeyEvent.VK_E:
                camera.moveAlongVAxis(-MOVEMENT_INCREMENT);
                break;
            case KeyEvent.VK_Q:
                camera.moveAlongVAxis(MOVEMENT_INCREMENT);
                break;
            case KeyEvent.VK_LEFT:
                camera.yaw(ROTATION_INCREMENT);
                break;
            case KeyEvent.VK_RIGHT:
                camera.yaw(-ROTATION_INCREMENT);
                break;
            case KeyEvent.VK_UP:
                camera.pitch(ROTATION_INCREMENT);
                break;
            case KeyEvent.VK_DOWN:
                camera.pitch(-ROTATION_INCREMENT);
                break;
            case KeyEvent.VK_SPACE:
                axesVisible = !axesVisible;
                break;
        }
    }

    public void drawWorldAxes() {
        gl.glUseProgram(axesRenderingProgram);

        modelMatrix.identity().scale(1000f).translate(0, 0.001f, 0);  // Translated slightly above to see axes better.
        int mLocation = gl.glGetUniformLocation(axesRenderingProgram, "m");
        gl.glUniformMatrix4fv(mLocation, 1, false, modelMatrix.get(buffer));

        int pLocation = gl.glGetUniformLocation(axesRenderingProgram, "p");
        gl.glUniformMatrix4fv(pLocation, 1, false, perspective.get(buffer));

        int vLocation = gl.glGetUniformLocation(axesRenderingProgram, "v");
        gl.glUniformMatrix4fv(vLocation, 1, false, camera.getViewMatrix().get(buffer));

        gl.glLineWidth(5);
        gl.glDrawArrays(GL_LINES, 0, 6);
    }

    public static void main(String[] args) { new Code(); }

    public void setupVertices() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        plane1 = new Model(gl, "a2/assets/plane1/plane1.obj", "a2/assets/plane1/plane1.jpg");
        plane2 = new Model(gl, "a2/assets/plane2/plane2.obj", "a2/assets/plane2/plane2.png");
        ground = new Model(gl, "a2/assets/ground/ground.obj", "a2/assets/ground/ground.png");
        castle = new Model(gl, ManualObjects.CASTLE_VERTEX_POSITIONS, ManualObjects.CASTLE_TEXTURE_COORDINATES, "a2/assets/castleroof.jpg");
        bush = new Model(gl, ManualObjects.BUSH_VERTICES, ManualObjects.BUSH_TEXTURE_COORDINATES, "a2/assets/bush.png");
    }

    // Unused interface methods.
    public void keyTyped(KeyEvent e) { }
    public void keyReleased(KeyEvent e) { }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }
    public void dispose(GLAutoDrawable drawable) { }
    public void mouseWheelMoved(MouseWheelEvent e) { }
}
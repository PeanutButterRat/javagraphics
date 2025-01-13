package a3;

import static com.jogamp.opengl.GL4.*;
import static java.awt.event.KeyEvent.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.*;

import org.joml.*;

import java.awt.event.*;
import java.lang.Math;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;


public class Code extends JFrame implements GLEventListener, KeyListener, MouseWheelListener, MouseListener, MouseMotionListener {
    long lastFrame;
    int modelShaderProgram;
    int axesShaderProgram;
    int skyboxShaderProgram;
    int oceanShaderProgram;
    int pointShaderProgram;
    private final int[] vao = new int[1];

    FloatBuffer buffer = Buffers.newDirectFloatBuffer(16);

    GLCanvas canvas;
    GL4 gl;

    boolean axesVisible = false;

    Camera camera = new Camera();

    final float FOV = (float) Math.toRadians(60);
    final float NEAR_CLIPPING = 0.1f;
    final float FAR_CLIPPING = 1000f;

    final float MOVEMENT_SPEED = 40f;
    final float ROTATION_SPEED = 1.35f;

    float totalTime = 0.0f;


    Vector2f mouse = new Vector2f();
    float[] globalAmbient = { 0.8f, 0.8f, 0.8f, 1.0f };
    float[] lightAmbient = { 1f, 1f, 1f, 1.0f };
    float[] lightDiffuse = { 0.5f, 0.5f, 0.5f, 0.5f };
    float[] lightSpecular = { 1.0f, 1.0f, 1.0f, 1.0f };
    float[] lightLocation = { -17.0f , 35.0f, -5.0f };

    float[] woodAmbient = { 0.812f, 0.365f, 0.071f, 1f };
    float[] woodDiffuse = { 0.92f, 0.486f, 0.071f, 1f };
    float[] woodSpecular = { 0.5f, 0.5f,  0.5f,  0.5f };
    float woodShininess = 2f;

    float[] parrotAmbient = { 0.812f, 0.365f, 0.071f, 1f };
    float[] parrotDiffuse = { 0.92f, 0.5f, 0.5f, 1f };
    float[] parrotSpecular = { 0.55f, 0.25f,  0.25f,  0.25f };
    float parrotShininess = 1f;

    float[] seagullAmbient = { 0.5f, 0.5f, 0.5f, 1f };
    float[] seagullDiffuse = { 0.5f, 0.5f, 0.5f, 1f };
    float[] seagullSpecular = { 0.2f, 0.2f,  0.2f,  0.2f };
    float seagullShininess = 1f;

    float[] oceanAmbient = { 0.0f, 0.0f, 0.0f, 1f };
    float[] oceanDiffuse = { 0.92f, 0.5f, 0.5f, 1f };
    float[] oceanSpecular = { 0.55f, 0.25f,  0.25f,  0.25f };
    float oceanShininess = 0.25f;
    
    boolean lightEnabled = true;
    float[] zeroArray = { 0.0f, 0.0f, 0.0f, 0.0f };


    Map<Integer, Integer> keys = new HashMap<>();
    Matrix4f perspective = new Matrix4f().identity();
    Matrix4fStack model = new Matrix4fStack(15);
    Matrix4f inverse = new Matrix4f().identity();

    Model ship;
    Model head;
    Model rwing;
    Model lwing;
    Model body;
    Model seagull;
    Skybox skybox;
    Plane ocean;


    public Code() {
        setTitle("Assignment #3 - Lighting, Materials, and Skyboxes by Eric Brown");
        setSize(900, 600);
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseWheelListener(this);
        canvas.addMouseMotionListener(this);
        this.add(canvas);
        this.setVisible(true);

        Animator animator = new Animator(canvas);
        animator.start();

        camera.moveAlongNAxis(-50);
        camera.moveAlongVAxis(20);
        camera.moveAlongUAxis(-15);
        camera.pitch(-0.2f);
        camera.yaw(-0.15f);

        lastFrame = System.currentTimeMillis();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = (GL4) GLContext.getCurrentGL();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);

        modelShaderProgram = Utils.createShaderProgram("a3/shaders/modelVertexShader.glsl", "a3/shaders/modelFragmentShader.glsl");
        axesShaderProgram = Utils.createShaderProgram("a3/shaders/axesVertexShader.glsl", "a3/shaders/axesFragmentShader.glsl");
        skyboxShaderProgram = Utils.createShaderProgram("a3/shaders/skyboxVertexShader.glsl", "a3/shaders/skyboxFragmentShader.glsl");
        oceanShaderProgram = Utils.createShaderProgram("a3/shaders/oceanVertexShader.glsl", "a3/shaders/oceanFragmentShader.glsl");
        pointShaderProgram = Utils.createShaderProgram("a3/shaders/pointVertexShader.glsl", "a3/shaders/pointFragmentShader.glsl");

        ship = new Model(gl, "a3/assets/ship/ship.obj", "a3/assets/ship/diffuse.png");
        head = new Model(gl, "a3/assets/parrot/head.obj", "a3/assets/parrot/head.png");
        body = new Model(gl, "a3/assets/parrot/body.obj", "a3/assets/parrot/body.png");
        lwing = new Model(gl, "a3/assets/parrot/lwing.obj", "a3/assets/parrot/wings.png");
        rwing = new Model(gl, "a3/assets/parrot/rwing.obj", "a3/assets/parrot/wings.png");
        seagull = new Model(gl, "a3/assets/seagull/seagull.obj", "a3/assets/seagull/diffuse.png");
        skybox = new Skybox(gl, "a3/assets/skybox");
        ocean = new Plane(gl, 1000);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        long currentFrame = System.currentTimeMillis();
        float deltaTime = (currentFrame - lastFrame) / 1000f;
        lastFrame = currentFrame;
        totalTime += deltaTime;

        // Handle input.
        Vector3f cameraMovement = new Vector3f(
                -(keys.getOrDefault(VK_A, 0) - keys.getOrDefault(VK_D, 0)),
                keys.getOrDefault(VK_Q, 0) - keys.getOrDefault(VK_E, 0),
                keys.getOrDefault(VK_W, 0) - keys.getOrDefault(VK_S, 0)
        ).mul(MOVEMENT_SPEED * deltaTime);

        Vector2f cameraRotation = new Vector2f(
                keys.getOrDefault(VK_UP, 0) - keys.getOrDefault(VK_DOWN, 0),
                keys.getOrDefault(VK_LEFT, 0) - keys.getOrDefault(VK_RIGHT, 0)
        ).mul(ROTATION_SPEED * deltaTime);

        camera.move(cameraMovement);
        camera.rotate(cameraRotation);

        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        // Draw the skybox.
        gl.glUseProgram(skyboxShaderProgram);
        setMatrices(skyboxShaderProgram);
        skybox.draw();

        gl.glUseProgram(modelShaderProgram);

        // Ship.
        model.identity();
        model.translation(0, -10, 0);  // To see world axes better.
        model.pushMatrix();

        // Math.sin(2 * Math.PI / PERIOD * totalTime) * AMPLITUDE
        model.translate(
                (float) (Math.sin(2 * Math.PI / 10 * totalTime) * 6),
                2 + (float) (Math.cos(2 * Math.PI / 17.2 * totalTime) * 2),
                0
        );

        model.pushMatrix();
        model.rotateXYZ(
                (float) (Math.sin(2 * Math.PI / 5 * totalTime) * 0.1),
                0,
                (float) (Math.cos(2 * Math.PI / 8.6 * totalTime) * 0.10)
        );
        setMatrices(modelShaderProgram);
        setNormalMatrix(modelShaderProgram);
        setLights(modelShaderProgram, woodAmbient, woodDiffuse, woodSpecular, woodShininess);
        ship.draw();

        // Seagull.
        model.pushMatrix();
        model.translate(-6.60376f, 34.3862f, 1.49887f);
        model.rotateY((float) Math.toRadians(-90));
        model.scale(2);
        setMatrices(modelShaderProgram);
        setNormalMatrix(modelShaderProgram);
        setLights(modelShaderProgram, seagullAmbient, seagullDiffuse, seagullSpecular, seagullShininess);
        seagull.draw();
        model.popMatrix(); // Seagull transforms.
        model.popMatrix(); // Ship rotation.

        // Parrot.
        model.pushMatrix();
        model.rotateY((float) Math.toRadians(-90));
        model.translate(
                20 + (float) (Math.sin(2 * Math.PI / 5 * totalTime) * 4),
                30 + (float) (Math.cos(2 * Math.PI / 8.6 * totalTime) * 2),
                0
        );
        model.rotateZ((float) (Math.sin(2 * Math.PI / 5 * totalTime) * 0.25));

        setMatrices(modelShaderProgram);
        setNormalMatrix(modelShaderProgram);
        setLights(modelShaderProgram, parrotAmbient, parrotDiffuse, parrotSpecular, parrotShininess);
        body.draw();

        float rotation = (float) (Math.sin(2 * Math.PI / 0.5 * totalTime) * Math.toRadians(35));
        model.pushMatrix();
        model.rotateZ(rotation);
        setMatrices(modelShaderProgram);
        setNormalMatrix(modelShaderProgram);
        setLights(modelShaderProgram, parrotAmbient, parrotDiffuse, parrotSpecular, parrotShininess);
        lwing.draw();
        model.popMatrix();

        model.pushMatrix();
        model.rotateZ(-rotation);
        setMatrices(modelShaderProgram);
        setNormalMatrix(modelShaderProgram);
        setLights(modelShaderProgram, parrotAmbient, parrotDiffuse, parrotSpecular, parrotShininess);
        rwing.draw();
        model.popMatrix();

        setMatrices(modelShaderProgram);
        setNormalMatrix(modelShaderProgram);
        setLights(modelShaderProgram, parrotAmbient, parrotDiffuse, parrotSpecular, parrotShininess);
        head.draw();
        model.popMatrix();

        // Ocean.
        model.scale(10000, 10, 10000);
        gl.glUseProgram(oceanShaderProgram);
        int resolutionLocation = gl.glGetUniformLocation(oceanShaderProgram, "resolution");
        gl.glUniform2f(resolutionLocation, 0.05f, 0.05f);
        int offsetLocation = gl.glGetUniformLocation(oceanShaderProgram, "offset");
        gl.glUniform2f(offsetLocation, -totalTime * 0.05f, 0);
        setMatrices(oceanShaderProgram);
        setNormalMatrix(oceanShaderProgram);
        setLights(oceanShaderProgram, oceanAmbient, oceanDiffuse, oceanSpecular, oceanShininess);
        ocean.draw();

        model.popMatrix();  // World translation.

        // Draw the world axes.
        if (axesVisible) {
            gl.glUseProgram(axesShaderProgram);
            model.identity().scale(1000f);  // Translated slightly above to see axes better.
            setMatrices(axesShaderProgram);
            gl.glLineWidth(5);
            gl.glDrawArrays(GL_LINES, 0, 6);
        }

        // Draw the light marker.
        if (lightEnabled) {
            gl.glDisable(GL_DEPTH_TEST);
            gl.glUseProgram(pointShaderProgram);
            gl.glPointSize(5);
            model.identity();
            setMatrices(pointShaderProgram);
            int positionLocation = gl.glGetUniformLocation(pointShaderProgram, "position");
            gl.glUniform3fv(positionLocation, 1, lightLocation, 0);
            gl.glDrawArrays(GL_POINTS, 0, 1);
            gl.glEnable(GL_DEPTH_TEST);
        }
    }

    private void setNormalMatrix(int shaderProgram) {
        model.invert(inverse);
        inverse.transpose(inverse);
        int normalsLocation = gl.glGetUniformLocation(shaderProgram, "norm_matrix");
        gl.glUniformMatrix4fv(normalsLocation, 1, false, inverse.get(buffer));
    }

    private void setLights(int shaderProgram, float[] ambient, float[] diffuse, float [] specular, float shininess) {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        int ambientLocation = gl.glGetUniformLocation(shaderProgram, "globalAmbient");
        int lightAmbientLocation = gl.glGetUniformLocation(shaderProgram, "light.ambient");
        int lightDiffuseLocation = gl.glGetUniformLocation(shaderProgram, "light.diffuse");
        int lightSpecularLocation = gl.glGetUniformLocation(shaderProgram, "light.specular");
        int lightPositionLocation = gl.glGetUniformLocation(shaderProgram, "light.position");
        int materialAmbientLocation = gl.glGetUniformLocation(shaderProgram, "material.ambient");
        int materialDiffuseLocation = gl.glGetUniformLocation(shaderProgram, "material.diffuse");
        int materialSpecularLocation = gl.glGetUniformLocation(shaderProgram, "material.specular");
        int materialShininessLocation = gl.glGetUniformLocation(shaderProgram, "material.shininess");

        gl.glProgramUniform4fv(shaderProgram, ambientLocation, 1, globalAmbient, 0);
        gl.glProgramUniform4fv(shaderProgram, materialAmbientLocation, 1, ambient, 0);
        gl.glProgramUniform4fv(shaderProgram, materialDiffuseLocation, 1, diffuse, 0);
        gl.glProgramUniform4fv(shaderProgram, materialSpecularLocation, 1, specular, 0);
        gl.glProgramUniform1f(shaderProgram, materialShininessLocation, shininess);

        if (lightEnabled) {
            gl.glProgramUniform4fv(shaderProgram, lightAmbientLocation, 1, lightAmbient, 0);
            gl.glProgramUniform4fv(shaderProgram, lightDiffuseLocation, 1, lightDiffuse, 0);
            gl.glProgramUniform4fv(shaderProgram, lightSpecularLocation, 1, lightSpecular, 0);
            gl.glProgramUniform3fv(shaderProgram, lightPositionLocation, 1, lightLocation, 0);
        } else {
            gl.glProgramUniform4fv(shaderProgram, lightAmbientLocation, 1, zeroArray, 0);
            gl.glProgramUniform4fv(shaderProgram, lightDiffuseLocation, 1, zeroArray, 0);
            gl.glProgramUniform4fv(shaderProgram, lightSpecularLocation, 1, zeroArray, 0);
            gl.glProgramUniform3fv(shaderProgram, lightPositionLocation, 1, zeroArray, 0);
        }
    }


    public void setMatrices(int shaderProgram) {
        int modelMatrixLocation = gl.glGetUniformLocation(shaderProgram, "m_matrix");
        int viewMatrixLocation = gl.glGetUniformLocation(shaderProgram, "v_matrix");
        int prospectiveMatrixLocation = gl.glGetUniformLocation(shaderProgram, "p_matrix");
        gl.glUniformMatrix4fv(modelMatrixLocation, 1, false, model.get(buffer));
        gl.glUniformMatrix4fv(viewMatrixLocation, 1, false, camera.getViewMatrix().get(buffer));
        gl.glUniformMatrix4fv(prospectiveMatrixLocation, 1, false, perspective.get(buffer));
    }

    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case VK_ESCAPE:
                System.exit(0);
                break;
            case VK_SPACE:
                axesVisible = !axesVisible;
                break;
            case VK_R:
                lightEnabled = !lightEnabled;
                break;
        }
        keys.put(event.getKeyCode(), 1);
    }


    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        float aspectRatio = (float) canvas.getWidth() / canvas.getHeight();
        perspective.setPerspective(FOV, aspectRatio, NEAR_CLIPPING, FAR_CLIPPING);
    }

    public void mouseWheelMoved(MouseWheelEvent event) {
        Vector3f change = camera.getNAxis().mul(-event.getWheelRotation());
        lightLocation[0] += change.x;
        lightLocation[1] += change.y;
        lightLocation[2] += change.z;
    }

    public void mousePressed(MouseEvent event) {
        mouse.x = event.getX();
        mouse.y = event.getY();
    }

    public void mouseDragged(MouseEvent event) {
        Vector2f distance = new Vector2f(event.getX(), event.getY()).sub(mouse);
        Vector3f change = new Vector3f();
        change.add(camera.getUAxis().mul(distance.x));
        change.add(camera.getVAxis().mul(-distance.y));
        change.div(5);
        mouse.x = event.getX();
        mouse.y = event.getY();
        lightLocation[0] += change.x;
        lightLocation[1] += change.y;
        lightLocation[2] += change.z;
    }

    public void keyReleased(KeyEvent event) { keys.put(event.getKeyCode(), 0); }
    public void keyTyped(KeyEvent event) { }
    public void dispose(GLAutoDrawable drawable) { }
    public void mouseEntered(MouseEvent event) { }
    public void mouseExited(MouseEvent event) { }
    public void mouseClicked(MouseEvent event) { }
    public void mouseMoved(MouseEvent event) { }
    public void mouseReleased(MouseEvent event) { }

    public static void main(String[] args) { new Code(); }
}
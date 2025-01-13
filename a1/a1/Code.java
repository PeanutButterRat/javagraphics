package a1;

import javax.swing.*;

import static com.jogamp.opengl.GL4.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.*;

import java.awt.*;
import java.awt.event.*;

public class Code extends JFrame implements GLEventListener, MouseWheelListener, KeyListener {
    private GLCanvas myCanvas;
    private int renderingProgram;
    private int vao[] = new int[1];
    private float x = 0.0f;
    private float y = 0.0f;
    private float angle = 0.0f;
    private float velocity = 0.8f;
    private boolean circularMovement = false;
    private long lastFrame = System.currentTimeMillis();

    private final float[] GREEN = {0.0f, 1.0f, 0.0f, 1.0f};
    private final float[] ORANGE = {0.91f, 0.541f, 0.137f, 1.0f};
    private final float[] BLUE = {0.137f, 0.788f, 0.91f, 1.0f};
    private float[] aPos = {0.0f, 0.0f};
    private float[] bPos = {0.0f, 0.0f};
    private float[] cPos = {0.0f, 0.0f};

    private float[] aColor;
    private float[] bColor;
    private float[] cColor;

    private float scale = 1.0f;
    private int colorMode = 0;
    private int rotationMode = 0;

    public Code() {
        setTitle("Lab #1 - OpenGL and JOGL by Eric Brown");
        setSize(600, 600);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        this.setVisible(true);

        // Add key binds.
        myCanvas.addKeyListener(this);
        this.addMouseWheelListener(this);

        JPanel top = new JPanel();
        this.add(top, BorderLayout.NORTH);

        // Add the required buttons to the screen.
        JButton movementButton = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                circularMovement = !circularMovement;
                angle = 0f;
                x = 0f;
                y = 0f;
            }
        });
        top.add(movementButton);
        movementButton.setText("Change Movement");

        JButton colorButton = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                colorMode = (colorMode + 1) % 3;
            }
        });
        top.add(colorButton);
        colorButton.setText("Change Color");

        Animator animator = new Animator(myCanvas);
        animator.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(renderingProgram);

        long currentFrame = System.currentTimeMillis();
        float deltaTime = (currentFrame - lastFrame) / 1000.0f;
        lastFrame = currentFrame;

        if (circularMovement) {
            angle += velocity * deltaTime;
            x = (float) (Math.cos(angle) * 0.75f);
            y = (float) (Math.sin(angle) * 0.75f);
        } else {
            x += velocity * deltaTime;
            if (x > 1.0f) {
                velocity = -0.8f;
            } else if (x < -1.0f) {
                velocity = 0.8f;
            }
        }

        if (rotationMode == 0) {
            aPos[0] = 0.2f * scale + x;
            aPos[1] = -0.1f * scale + y;
            bPos[0] = -0.2f * scale + x;
            bPos[1] = 0.0f * scale + y;
            cPos[0] = 0.2f * scale + x;
            cPos[1] = 0.1f * scale + y;
        } else if (rotationMode == 1) {
            aPos[0] = -0.1f * scale + x;
            aPos[1] = -0.2f * scale + y;
            bPos[0] = 0.0f * scale + x;
            bPos[1] = 0.2f * scale + y;
            cPos[0] = 0.1f * scale + x;
            cPos[1] = -0.2f * scale + y;
        } else if (rotationMode == 2) {
            aPos[0] = -0.2f * scale + x;
            aPos[1] = -0.1f * scale + y;
            bPos[0] = 0.2f * scale + x;
            bPos[1] = 0.0f * scale + y;
            cPos[0] = -0.2f * scale + x;
            cPos[1] = 0.1f * scale + y;
        } else {
            aPos[0] = -0.1f * scale + x;
            aPos[1] = 0.2f * scale + y;
            bPos[0] = 0.0f * scale + x;
            bPos[1] = -0.2f * scale + y;
            cPos[0] = 0.1f * scale + x;
            cPos[1] = 0.2f * scale + y;
        }

        int aPosLocation = gl.glGetUniformLocation(renderingProgram,  "a_pos");
        gl.glProgramUniform4f(renderingProgram, aPosLocation, aPos[0], aPos[1], 0.0f, 1.0f);
        int bPosLocation = gl.glGetUniformLocation(renderingProgram,  "b_pos");
        gl.glProgramUniform4f(renderingProgram, bPosLocation, bPos[0], bPos[1], 0.0f, 1.0f);
        int cPosLocation = gl.glGetUniformLocation(renderingProgram,  "c_pos");
        gl.glProgramUniform4f(renderingProgram, cPosLocation, cPos[0], cPos[1], 0.0f, 1.0f);

        if (colorMode == 0) {
            aColor = GREEN;
            bColor = GREEN;
            cColor = GREEN;
        } else if (colorMode == 1) {
            aColor = ORANGE;
            bColor = ORANGE;
            cColor = ORANGE;
        } else {
            aColor = GREEN;
            bColor = BLUE;
            cColor = ORANGE;
        }

        int aColorLocation = gl.glGetUniformLocation(renderingProgram,  "a_color");
        gl.glProgramUniform4f(renderingProgram, aColorLocation, aColor[0], aColor[1], aColor[2], aColor[3]);
        int bColorLocation = gl.glGetUniformLocation(renderingProgram,  "b_color");
        gl.glProgramUniform4f(renderingProgram, bColorLocation, bColor[0], bColor[1], bColor[2], bColor[3]);
        int cColorLocation = gl.glGetUniformLocation(renderingProgram,  "c_color");
        gl.glProgramUniform4f(renderingProgram, cColorLocation, cColor[0], cColor[1], cColor[2], cColor[3]);
        gl.glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        System.out.println("JOGL version: " + Package.getPackage("com.jogamp.opengl").getImplementationVersion());
        System.out.println("OpenGL version: " + gl.glGetString(GL_VERSION));

        renderingProgram = Utils.createShaderProgram("a1/vertShader.glsl", "a1/fragShader.glsl");
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_1) {
            rotationMode = (rotationMode + 1) % 4;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scale -= e.getWheelRotation() * 0.1f;
        scale = Math.max(0.1f, scale);
        scale = Math.min(8.0f, scale);
    }

    public static void main(String[] args) { new Code(); }

    // Unneeded interface methods.
    public void keyTyped(KeyEvent e) { }
    public void keyReleased(KeyEvent e) { }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }
    public void dispose(GLAutoDrawable drawable) { }
}
package a4;

import javax.swing.*;
import java.lang.Math;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.common.nio.Buffers;
import org.joml.*;
import java.awt.event.*;

import static java.awt.event.KeyEvent.*;
import com.jogamp.opengl.GLContext;

import com.jogamp.opengl.util.Animator;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;


public class Code extends JFrame implements GLEventListener, KeyListener, MouseWheelListener, MouseListener, MouseMotionListener {
	GLCanvas canvas;
	Map<Integer, Integer> keys = new HashMap<>();
	int[] vao = new int[1];
	int [] shadowTexture = new int[1];
	int [] shadowBuffer = new int[1];

	final float FOV = (float) Math.toRadians(60);
	final float NEAR_CLIPPING = 0.1f;
	final float FAR_CLIPPING = 1000f;

	final float[] GLOBAL_AMBIENT = { 0.7f, 0.7f, 0.7f, 1.0f };
	final float[] LIGHT_AMBIENT = { 0.2f, 0.2f, 0.2f, 0.2f };
	final float[] LIGHT_DIFFUSE = { 1.0f, 1.0f, 1.0f, 1.0f };
	final float[] LIGHT_SPECULAR = { 1.0f, 1.0f, 1.0f, 1.0f };

	float[] materialAmbient = { 0.2f, 0.2f, 0.2f, 0.2f };
	float[] materialDiffuse = { 0.2f, 0.2f, 0.2f, 0.2f };
	float[] materialSpecular = { 0.2f, 0.2f, 0.2f, 0.2f };
	float materialShininess = 0.25f;

	float aspectRatio;
	Matrix4f lightViewMatrix = new Matrix4f();
	Matrix4f lightPerspectiveMatrix = new Matrix4f();
	Matrix4f shadowMVP = new Matrix4f();
	Matrix4f b = new Matrix4f();
	Camera camera = new Camera();

	FloatBuffer buffer = Buffers.newDirectFloatBuffer(16);
	Matrix4f perspectiveMatrix = new Matrix4f();
	Matrix4f viewMatrix = new Matrix4f();
	Matrix4f modelMatrix = new Matrix4f();
	Matrix4f inverseTranspositionMatrix = new Matrix4f();

	int modelMatrixUniformLocation;
	int viewMatrixUniformLocation;
	int perspectiveMatrixUniformLocation;
	int normalMatrixUniformLocation;
	int shadowMVPUniformLocation;
	int globalAmbientUniformLocation;
	int lightAmbientUniformLocation;
	int lightDiffuseUniformLocation;
	int lightSpecularUniformLocation;
	int lightPositionUniformLocation;
	int materialAmbientUniformLocation;
	int materialDiffuseUniformLocation;
	int materialSpecularUniformLocation;
	int materialShininessUniformLocation;
	int bumpMappedUniformLocation;
	int lightEnabledUniformLocation;
	int alphaUniformLocation;
	Vector3f light = new Vector3f();
	float[] lightPosition = new float[3];

	long lastFrame;
	long currentFrame;;
	float deltaTime;

	Skybox skybox;
	Model ground;
	Model tvBody;
	Model tvScreen;

	Model cabin;
	Model table;
	Model stand;
	Model chair;
	Model trees;

	Model vase;
	Model windows;

	int[] noiseTextures = new int[10];
	final float TV_FPS = 20;
	float tvTime = 0;
	final Vector3f ORIGIN = new Vector3f(0, 0, 0);
	final Vector3f UP = new Vector3f(0, 1, 0);

	Vector2f mouse = new Vector2f();
	boolean lightEnabled = true;
	boolean axesVisible = false;
	boolean stereoscopyEnabled = false;

	final float MOVEMENT_SPEED = 5f;
	final float ROTATION_SPEED = 1.5f;
	final float IOD = 0.2f;

	int shadowShaderProgram;
	int modelShaderProgram;
	int skyboxShaderProgram;
	int axesShaderProgram;
	int pointShaderProgram;

	GL4 gl;

	public Code() {
		setTitle("Assignment #4 - Advanced Techniques by Eric Brown");
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
	}

	public void init(GLAutoDrawable drawable) {
		gl = (GL4) GLContext.getCurrentGL();
		shadowShaderProgram = Utils.createShaderProgram("a4/shaders/shadowVertexShader.glsl", "a4/shaders/shadowFragmentShader.glsl");
		modelShaderProgram = Utils.createShaderProgram("a4/shaders/modelVertexShader.glsl", "a4/shaders/modelFragmentShader.glsl");
		skyboxShaderProgram = Utils.createShaderProgram("a4/shaders/skyboxVertexShader.glsl", "a4/shaders/skyboxFragmentShader.glsl");
		axesShaderProgram = Utils.createShaderProgram("a4/shaders/axesVertexShader.glsl", "a4/shaders/axesFragmentShader.glsl");
		pointShaderProgram = Utils.createShaderProgram("a4/shaders/pointVertexShader.glsl", "a4/shaders/pointFragmentShader.glsl");

		perspectiveMatrix.identity().setPerspective(FOV, aspectRatio, NEAR_CLIPPING, FAR_CLIPPING);
		b.set(
				0.5f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.5f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.5f, 0.0f,
				0.5f, 0.5f, 0.5f, 1.0f
		);

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		buildShadowBuffer();

		skybox = new Skybox(gl, "a4/assets/skybox");
		ground = new Model(gl, "a4/assets/ground/ground.obj", "a4/assets/ground/ground.png");
		vase = new Model(gl, "a4/assets/vase/vase.obj", "a4/assets/vase/vase.png");
		tvBody = new Model(gl, "a4/assets/tv/body.obj", "a4/assets/tv/tv.png");
		tvScreen = new Model(gl, "a4/assets/tv/screen.obj", "a4/assets/tv/tv.png");
		cabin = new Model(gl, "a4/assets/cabin/cabin.obj", "a4/assets/cabin/cabin.png");
		table = new Model(gl, "a4/assets/table/table.obj", "a4/assets/table/table.png");
		stand = new Model(gl, "a4/assets/stand/stand.obj", "a4/assets/stand/stand.png");
		chair = new Model(gl, "a4/assets/chair/chair.obj", "a4/assets/chair/chair.jpg");
		windows = new Model(gl, "a4/assets/windows/windows.obj", "a4/assets/windows/windows.png");
		trees = new Model(gl, "a4/assets/trees/trees.obj", "a4/assets/trees/trees.jpeg");

		for (int i = 0; i < noiseTextures.length; i++) {
			noiseTextures[i] = Noise.generateNoiseTexture(gl);
		}

		camera.setPosition(0, 5, 20);
		camera.pitch(-0.25f);
		light.set(0, 15, 10);

		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendEquation(GL_FUNC_ADD);

	}

	public void display(GLAutoDrawable drawable) {
		lastFrame = currentFrame;
		currentFrame = System.currentTimeMillis();
		deltaTime = (currentFrame - lastFrame) / 1000f;

		tvTime += deltaTime;
		tvScreen.setTexture(noiseTextures[((int) (tvTime / (1 / TV_FPS))) % noiseTextures.length]);
		tvTime %= 1;

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

		lightViewMatrix.identity().setLookAt(light, ORIGIN, UP);
		lightPerspectiveMatrix.identity().setPerspective(FOV, aspectRatio, NEAR_CLIPPING, FAR_CLIPPING);

		gl.glColorMask(true, true, true, true);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL_COLOR_BUFFER_BIT);

		if (stereoscopyEnabled) {
			gl.glClear(GL_DEPTH_BUFFER_BIT);
			gl.glClear(GL_COLOR_BUFFER_BIT);

			gl.glColorMask(true, false, false, false);
			render(-IOD / 2.0f);

			gl.glClear(GL_DEPTH_BUFFER_BIT);

			gl.glColorMask(false, true, true, false);
			render(IOD / 2.0f);
		} else {
			render(0.0f);
		}
	}

	public void render(float viewOffset) {
		viewMatrix.set(camera.getViewMatrix(viewOffset));

		gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);
		gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTexture[0], 0);
		gl.glDrawBuffer(GL_NONE);
		gl.glEnable(GL_DEPTH_TEST);

		// Shadow artifact reduction.
		gl.glEnable(GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(3.0f, 5.0f);

		drawObjects(shadowShaderProgram);  // Pass one.

		gl.glDisable(GL_POLYGON_OFFSET_FILL);
		gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTexture[0]);
		gl.glDrawBuffer(GL_FRONT);

		modelMatrix.identity();
		gl.glUseProgram(skyboxShaderProgram);
		installMatrices(skyboxShaderProgram);
		skybox.draw();

		drawObjects(modelShaderProgram);  // Pass two.

		if (axesVisible) drawWorldAxes();
		if (lightEnabled) drawLightLocationPoint();
	}
	
	public void drawObjects(int shaderProgram) {
		gl.glUseProgram(shaderProgram);
		gl.glClear(GL_DEPTH_BUFFER_BIT);

		modelMatrix.identity();
		installMatrices(shaderProgram);
		installLights(shaderProgram,true, 1.0f);
		vase.draw();

		installMatrices(shaderProgram);
		installLights(shaderProgram,false, 1.0f);

		ground.draw();
		cabin.draw();
		trees.draw();
		tvBody.draw();
		tvScreen.draw();
		table.draw();
		stand.draw();
		chair.draw();

		gl.glEnable(GL_BLEND);
		installLights(shaderProgram, false, 0.7f);
		windows.draw();
		gl.glDisable(GL_BLEND);
	}

	private void buildShadowBuffer() {
		int x = canvas.getWidth();
		int y = canvas.getHeight();
		gl.glGenFramebuffers(1, shadowBuffer, 0);
		gl.glGenTextures(1, shadowTexture, 0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTexture[0]);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, x, y, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}
	
	public void installLights(int renderingProgram, boolean bumpMapped, float alpha) {
		lightPosition[0] = light.x();
		lightPosition[1] = light.y();
		lightPosition[2] = light.z();
		
		globalAmbientUniformLocation = gl.glGetUniformLocation(renderingProgram, "global_ambient");
		lightAmbientUniformLocation = gl.glGetUniformLocation(renderingProgram, "light.ambient");
		lightDiffuseUniformLocation = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
		lightSpecularUniformLocation = gl.glGetUniformLocation(renderingProgram, "light.specular");
		lightPositionUniformLocation = gl.glGetUniformLocation(renderingProgram, "light.position");
		materialAmbientUniformLocation = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		materialDiffuseUniformLocation = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		materialSpecularUniformLocation = gl.glGetUniformLocation(renderingProgram, "material.specular");
		materialShininessUniformLocation = gl.glGetUniformLocation(renderingProgram, "material.shininess");
		bumpMappedUniformLocation = gl.glGetUniformLocation(renderingProgram, "bump_mapped");
		lightEnabledUniformLocation = gl.glGetUniformLocation(renderingProgram, "light_enabled");
		alphaUniformLocation = gl.glGetUniformLocation(renderingProgram, "alpha");

		gl.glProgramUniform4fv(renderingProgram, globalAmbientUniformLocation, 1, GLOBAL_AMBIENT, 0);
		gl.glProgramUniform4fv(renderingProgram, lightAmbientUniformLocation, 1, LIGHT_AMBIENT, 0);
		gl.glProgramUniform4fv(renderingProgram, lightDiffuseUniformLocation, 1, LIGHT_DIFFUSE, 0);
		gl.glProgramUniform4fv(renderingProgram, lightSpecularUniformLocation, 1, LIGHT_SPECULAR, 0);
		gl.glProgramUniform3fv(renderingProgram, lightPositionUniformLocation, 1, lightPosition, 0);
		gl.glProgramUniform4fv(renderingProgram, materialAmbientUniformLocation, 1, materialAmbient, 0);
		gl.glProgramUniform4fv(renderingProgram, materialDiffuseUniformLocation, 1, materialDiffuse, 0);
		gl.glProgramUniform4fv(renderingProgram, materialSpecularUniformLocation, 1, materialSpecular, 0);
		gl.glProgramUniform1f(renderingProgram, materialShininessUniformLocation, materialShininess);
		gl.glProgramUniform1f(renderingProgram, bumpMappedUniformLocation, (bumpMapped) ? 1 : 0);
		gl.glProgramUniform1f(renderingProgram, lightEnabledUniformLocation, (lightEnabled) ? 1 : 0);
		gl.glProgramUniform1f(renderingProgram, alphaUniformLocation, alpha);
	}

	public void installMatrices(int shaderProgram) {
		modelMatrixUniformLocation = gl.glGetUniformLocation(shaderProgram, "m_matrix");
		viewMatrixUniformLocation = gl.glGetUniformLocation(shaderProgram, "v_matrix");
		perspectiveMatrixUniformLocation = gl.glGetUniformLocation(shaderProgram, "p_matrix");
		normalMatrixUniformLocation = gl.glGetUniformLocation(shaderProgram, "normals_matrix");
		shadowMVPUniformLocation = gl.glGetUniformLocation(shaderProgram, "shadow_mvp");

		modelMatrix.invert(inverseTranspositionMatrix);
		inverseTranspositionMatrix.transpose(inverseTranspositionMatrix);

		shadowMVP.identity();
		if (shaderProgram == modelShaderProgram) shadowMVP.mul(b);
		shadowMVP.mul(lightPerspectiveMatrix);
		shadowMVP.mul(lightViewMatrix);
		shadowMVP.mul(modelMatrix);

		gl.glUniformMatrix4fv(modelMatrixUniformLocation, 1, false, modelMatrix.get(buffer));
		gl.glUniformMatrix4fv(viewMatrixUniformLocation, 1, false, viewMatrix.get(buffer));
		gl.glUniformMatrix4fv(perspectiveMatrixUniformLocation, 1, false, perspectiveMatrix.get(buffer));
		gl.glUniformMatrix4fv(normalMatrixUniformLocation, 1, false, inverseTranspositionMatrix.get(buffer));
		gl.glUniformMatrix4fv(shadowMVPUniformLocation, 1, false, shadowMVP.get(buffer));
	}

	public void drawWorldAxes() {
		gl.glUseProgram(axesShaderProgram);
		modelMatrix.identity().scale(1000f);
		installMatrices(axesShaderProgram);
		gl.glLineWidth(5);
		gl.glDrawArrays(GL_LINES, 0, 6);
	}

	public void drawLightLocationPoint() {
		gl.glDisable(GL_DEPTH_TEST);
		gl.glUseProgram(pointShaderProgram);
		gl.glPointSize(5);
		modelMatrix.identity();
		installMatrices(pointShaderProgram);
		int positionLocation = gl.glGetUniformLocation(pointShaderProgram, "position");
		gl.glUniform3fv(positionLocation, 1, lightPosition, 0);
		gl.glDrawArrays(GL_POINTS, 0, 1);
		gl.glEnable(GL_DEPTH_TEST);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		aspectRatio = (float) canvas.getWidth() / (float) canvas.getHeight();
		perspectiveMatrix.identity().setPerspective(FOV, aspectRatio, NEAR_CLIPPING, FAR_CLIPPING);
		buildShadowBuffer();
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
			case VK_F:
				stereoscopyEnabled = !stereoscopyEnabled;
				break;
		}
		keys.put(event.getKeyCode(), 1);
	}

	public void keyReleased(KeyEvent event) {
		keys.put(event.getKeyCode(), 0);
	}

	public void mouseWheelMoved(MouseWheelEvent event) {
		light.add(camera.getNAxis().mul(-event.getWheelRotation()));
	}

	public void mousePressed(MouseEvent event) {
		if (!lightEnabled) return;
		mouse.x = event.getX();
		mouse.y = event.getY();
	}

	public void mouseDragged(MouseEvent event) {
		if (!lightEnabled) return;
		Vector2f distance = new Vector2f(event.getX(), event.getY()).sub(mouse);
		Vector3f change = new Vector3f();
		change.add(camera.getUAxis().mul(distance.x));
		change.add(camera.getVAxis().mul(-distance.y));
		change.div(5);
		mouse.x = event.getX();
		mouse.y = event.getY();
		light.add(change);
	}

	public void keyTyped(KeyEvent event) { }
	public void dispose(GLAutoDrawable drawable) { }
	public void mouseEntered(MouseEvent event) { }
	public void mouseExited(MouseEvent event) { }
	public void mouseClicked(MouseEvent event) { }
	public void mouseMoved(MouseEvent event) { }
	public void mouseReleased(MouseEvent event) { }
	public static void main(String[] args) { new Code(); }
}
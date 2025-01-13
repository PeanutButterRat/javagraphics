package a4;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Matrix4f;


public class Camera {
    private Vector3f position = new Vector3f();

    private Vector3f u = new Vector3f(1, 0, 0);
    private Vector3f v = new Vector3f(0, 1, 0);
    private Vector3f n = new Vector3f(0, 0, -1);
    
    Matrix4f translation = new Matrix4f();
    Matrix4f rotation = new Matrix4f();
    Matrix4f view = new Matrix4f();

    Matrix4f getViewMatrix(float viewOffsetX) {
        translation.set(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                -(position.x + viewOffsetX), -position.y, -position.z, 1
        );

        rotation.set(
                u.x, v.x, -n.x, 0,
                u.y, v.y, -n.y, 0,
                u.z, v.z, -n.z, 0,
                0, 0, 0, 1
        );

        view.set(rotation);
        view.mul(translation);
        return view;
    }


    public void moveAlongUAxis(float amount) {
        position.add(new Vector3f(u).mul(amount));
    }

    public void moveAlongVAxis(float amount) {
        position.add(new Vector3f(v).mul(amount));
    }

    public void moveAlongNAxis(float amount) {
        position.add(new Vector3f(n).mul(amount));
    }

    public Vector3f getUAxis() {
        return new Vector3f(u);
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public Vector3f getNAxis() {
        return new Vector3f(n);
    }

    public Vector3f getVAxis() {
        return new Vector3f(v);
    }

    public void move(Vector3f amount) {
        moveAlongUAxis(amount.x);
        moveAlongVAxis(amount.y);
        moveAlongNAxis(amount.z);
    }

    public void rotate(Vector2f amount) {
        pitch(amount.x);
        yaw(amount.y);
    }

    public void pitch(float angle) {
        v.rotateAxis(angle, u.x, u.y, u.z);
        n.rotateAxis(angle, u.x, u.y, u.z);
    }

    public void yaw(float angle) {
        // For global yaw.
         Vector3f worldUp = new Vector3f(0, 1, 0);
         u.rotateAxis(angle, worldUp.x, worldUp.y, worldUp.z);
         v.rotateAxis(angle, worldUp.x, worldUp.y, worldUp.z);
         n.rotateAxis(angle, worldUp.x, worldUp.y, worldUp.z);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }
}

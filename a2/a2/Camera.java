package a2;

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

    Matrix4f getViewMatrix() {
        translation.set(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                -position.x, -position.y, -position.z, 1
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


    void moveAlongUAxis(float amount) {
        position.add(new Vector3f(u).mul(amount));
    }

    void moveAlongVAxis(float amount) {
        position.add(new Vector3f(v).mul(amount));
    }

    void moveAlongNAxis(float amount) {
        position.add(new Vector3f(n).mul(amount));
    }

    void pitch(float angle) {
        v.rotateAxis(angle, u.x, u.y, u.z);
        n.rotateAxis(angle, u.x, u.y, u.z);
    }

    void yaw(float angle) {
        // For global yaw.
        // Vector3f worldUp = new Vector3f(0, 1, 0);
        // u.rotateAxis(angle, worldUp.x, worldUp.y, worldUp.z);
        // v.rotateAxis(angle, worldUp.x, worldUp.y, worldUp.z);
        // n.rotateAxis(angle, worldUp.x, worldUp.y, worldUp.z);

        // For local yaw.
         u.rotateAxis(angle, v.x, v.y, v.z);
         n.rotateAxis(angle, v.x, v.y, v.z);
    }

    void roll(float angle) {
        v.rotateAxis(angle, n.x, n.y, n.z);
        u.rotateAxis(angle, n.x, n.y, n.z);
    }
}

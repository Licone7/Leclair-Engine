package Leclair.graphics.scene;

import Leclair.input.InputData;
import Leclair.input.key.Keys;
import Leclair.math.MathUtilities;
import Leclair.math.Matrix4x4;
import Leclair.math.Vector3;
import Leclair.window.WindowInfo;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/**
 * 
 */
public class Camera {

    Matrix4f projectionMatrix = new Matrix4f();
    Matrix4x4 projMatrix = new Matrix4x4();
    Quaternionf orientation = new Quaternionf();
    Vector3 position = new Vector3(0, 2, 5);
    Vector3 forward;
    Vector3 up;
    boolean updateAspectRation = true;
    float mouseX;
    float mouseY;
    float deltaX;
    float deltaY;
    boolean viewing;
    public static final float FOV = MathUtilities.asRadians(70);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;

    public Camera() {
        position.negate();
        // GLFW.glfwSetMouseButtonCallback(WindowInfo.getNativeWindow(), (long window4,
        // int button, int action, int mods) -> {
        // if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
        // viewing = true;
        // } else {
        // viewing = false;
        // }
        // });
        projectionMatrix.setPerspective((float) Math.toRadians(70), (float) 640 / 480, 0.1f, 1000.0f)
                .rotate(orientation).translate(position.getX(), position.getY(), position.getZ());

    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public void update() {
        if (viewing) {
            final float deltaX = (float) WindowInfo.getMousePosX() - mouseX;
            final float deltaY = (float) WindowInfo.getMousePosY() - mouseY;
            orientation.rotateLocalX(deltaY * 0.01f * 2);
            orientation.rotateLocalY(deltaX * 0.01f * 2);
            orientation.z = 0;
            mouseX = (int) WindowInfo.getMousePosX();
            mouseY = (int) WindowInfo.getMousePosY();
            if (InputData.isKeyPressed(Keys.KEY_A))
                // speed = 10f;
                position.add(0.06f, 0, 0);
            if (InputData.isKeyPressed(Keys.KEY_D))
                // rotateZ -= 1f;
                position.add(-0.06f, 0, 0);
            if (InputData.isKeyPressed(Keys.KEY_W))
                // rotateZ -= 1f;
                position.add(0, 0, 0.06f);
            if (InputData.isKeyPressed(Keys.KEY_S))
                position.add(0, 0, -0.06f);
            if (InputData.isKeyPressed(Keys.KEY_Q)) {
                // rotateZ += 1f;
                position.add(0, -0.06f, 0);
            }
            if (InputData.isKeyPressed(Keys.KEY_Z)) {
                position.add(0, 0.06f, 0);
                // rotateZ -= 1f;
                // position.add(orientation.positiveZ(new Vector3f()).mul(dt * speed));
            }
            projectionMatrix.setPerspective((float) Math.toRadians(70), (float) 640 / 480, 0.1f, 1000.0f)
                    .rotate(orientation).translate(position.getX(), position.getY(), position.getZ());
        }
        // orientation.rotateLocalX(rotateZ * 0.01f * speed);
    }

}

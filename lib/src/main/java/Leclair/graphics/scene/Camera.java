package Leclair.graphics.scene;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import Leclair.input.key.KeyHandler;
import Leclair.input.key.Keys;
import Leclair.input.mouse.CursorHandler;
import Leclair.input.mouse.MouseButtonHandler;
import Leclair.input.mouse.MouseButtons;
import Leclair.math.MathUtilities;
import Leclair.math.Matrix4x4;
import Leclair.math.Vector3;

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
        projectionMatrix.setPerspective((float) Math.toRadians(70), (float) 640 / 480, 0.1f, 1000.0f)
                .rotate(orientation).translate(position.getX(), position.getY(), position.getZ());

    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public void update() {
        if (MouseButtonHandler.isMouseButtonPressed(MouseButtons.MOUSE_BUTTON_LEFT) == true) {
            viewing = true;
        } else {
            viewing = false;
        }
        if (viewing) {
            final float deltaX = CursorHandler.getCursorXPosition() - mouseX;
            final float deltaY = CursorHandler.getCursorYPosition() - mouseY;
            orientation.rotateLocalX(deltaY * 0.01f).rotateLocalY(deltaX * 0.01f);
        }
        mouseX = (int) CursorHandler.getCursorXPosition();
        mouseY = (int) CursorHandler.getCursorYPosition();
        if (KeyHandler.isKeyPressed(Keys.KEY_A))
            position.add(0.16f, 0, 0);
        if (KeyHandler.isKeyPressed(Keys.KEY_D))
            position.add(-0.16f, 0, 0);
        if (KeyHandler.isKeyPressed(Keys.KEY_W))
            position.add(0, 0, 0.16f);
        if (KeyHandler.isKeyPressed(Keys.KEY_S))
            position.add(0, 0, -0.16f);
        if (KeyHandler.isKeyPressed(Keys.KEY_Q))
            position.add(0, -0.16f, 0);
        if (KeyHandler.isKeyPressed(Keys.KEY_Z))
            position.add(0, 0.16f, 0);
        orientation.z = 0;
        final AxisAngle4f dest = new AxisAngle4f();
        orientation.get(dest);
        final float x = dest.angle;
        final float y = dest.x;
        final float z = dest.y; // Dude
        final float w = dest.z;
        projectionMatrix.setPerspective((float) Math.toRadians(70), (float) 640 / 480, 0.1f, 1000.0f)
                .rotate(x, y, z, w).translate(position.getX(), position.getY(), position.getZ());
    }
}
package Leclair.window.glfw;

import Leclair.application.ApplicationStructure;
import Leclair.input.key.KeyHandler;
import Leclair.input.mouse.CursorHandler;
import Leclair.input.mouse.MouseButtonHandler;
import Leclair.input.mouse.MouseButtons;
import Leclair.window.Window;
import Leclair.window.WindowInfo;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.Callbacks;

public class GlfwWindow implements Window {

    static long window;

    @Override
    public void init() {
        if (!glfwInit())
            throw new IllegalStateException();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        window = glfwCreateWindow(WindowInfo.getWidth(), WindowInfo.getHeight(), WindowInfo.getTitle(), 0, 0);
        if (window == 0) {
            throw new RuntimeException();
        }
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                KeyHandler.KEY_PRESS = true;
                KeyHandler.CHAR = (char) key;
            } else if (action == GLFW_RELEASE) {
                KeyHandler.KEY_PRESS = false;
            }
        });
        glfwSetMouseButtonCallback(window,
                (final long window, final int button, final int action, final int mods) -> {
                    if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                        MouseButtonHandler.MOUSE_BUTTON_PRESS = true;
                        System.out.println(button);
                        switch (button) {
                            case GLFW_MOUSE_BUTTON_LEFT:
                                MouseButtonHandler.MOUSE_BUTTON = MouseButtons.MOUSE_BUTTON_LEFT;
                                break;
                            case GLFW_MOUSE_BUTTON_RIGHT:
                                MouseButtonHandler.MOUSE_BUTTON = MouseButtons.MOUSE_BUTTON_RIGHT;
                                break;
                            case GLFW_MOUSE_BUTTON_MIDDLE:
                                MouseButtonHandler.MOUSE_BUTTON = MouseButtons.MOUSE_BUTTON_MIDDLE;
                                break;
                        }
                    } else {
                        MouseButtonHandler.MOUSE_BUTTON_PRESS = false;
                    }
                });
        glfwSetCursorPosCallback(window,
                (final long window, final double xpos, final double ypos) -> {
                    CursorHandler.cursorXPosition = (float) xpos;
                    CursorHandler.cursorYPosition = (float) ypos;
                });
    }

    @Override
    public void loop() {
        if (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
        } else {
            ApplicationStructure.stop();
        }
    }

    @Override
    public void show() {
        glfwShowWindow(window);
    }

    @Override
    public long getWHandle() {
        return window;
    }

    @Override
    public void destroy() {
        Callbacks.glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
    }
}
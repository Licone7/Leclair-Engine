package Leclair.window.glfw;

import Leclair.application.ApplicationInfo;
import Leclair.application.ApplicationStructure;
import Leclair.window.Window;
import Leclair.window.WindowInfo;

import static org.lwjgl.glfw.GLFW.*;

public class GlfwWindow implements Window {

    static long window;

    @Override
    public void init() {
        if (!glfwInit())
            throw new IllegalStateException();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); 

        // Create the window
        window = glfwCreateWindow(WindowInfo.getWidth(), WindowInfo.getHeight(), WindowInfo.getTitle(), 0, 0);
        if (window == 0)
            throw new RuntimeException();
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
        // TODO Auto-generated method stub
        return window;
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}

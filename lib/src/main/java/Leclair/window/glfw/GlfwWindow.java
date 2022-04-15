package Leclair.window.glfw;

import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_NO_API;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_API;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import Leclair.application.ApplicationStructure;
import Leclair.asset.AssetLoader;
import Leclair.graphics.GraphicsInfo;
import Leclair.input.InputData;
import Leclair.window.Window;
import Leclair.window.WindowInfo;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class GlfwWindow implements Window {

    long window;
    GLFWCursorPosCallback cba;

    public GlfwWindow() {

    }

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glfwInit();
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
            if (GraphicsInfo.getRenderer() == GraphicsInfo.OPENGL) {
                glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
            }
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            window = glfwCreateWindow(WindowInfo.getWidth(), WindowInfo.getHeight(), WindowInfo.getTitle(), 0L, 0L);
            if (window == 0) {
                throw new IllegalStateException("Cannot create a window");
            }
            final IntBuffer pWidth = stack.mallocInt(1);
            final IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
            glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
                if (action != GLFW_PRESS) {
                    InputData.KEY_DOWN = false;
                } else if (action == GLFW_PRESS) {
                    InputData.KEY_DOWN = true;
                    InputData.CHAR = (char) key;
                }
            });
            final GLFWCursorPosCallbackI cb = new GLFWCursorPosCallbackI() {
                public void invoke(final long window, final double xpos, final double ypos) {
                    WindowInfo.setMousePosX(xpos);
                    WindowInfo.setMousePosY(ypos);
                }
            };
            cba = GLFWCursorPosCallback.create(cb);
            glfwSetCursorPosCallback(window, cba);
            glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
                WindowInfo.setWidth(width);
                WindowInfo.setHeight(height);
            });
            final IntBuffer w = stack.mallocInt(1);
            final IntBuffer h = stack.mallocInt(1);
            final IntBuffer comp = stack.mallocInt(1);
            final ByteBuffer icon16 = AssetLoader.importAsBinary("textures/bond.jpg", 2048);
            final ByteBuffer icon32 = AssetLoader.importAsBinary("textures/bond.jpg", 4096);
            try (GLFWImage.Buffer icons = GLFWImage.malloc(2)) {
                final ByteBuffer pixels16 = STBImage.stbi_load_from_memory(icon16, w, h, comp, 4);
                icons.position(0).width(w.get(0)).height(h.get(0)).pixels(pixels16);
                final ByteBuffer pixels32 = STBImage.stbi_load_from_memory(icon32, w, h, comp, 4);
                icons.position(1).width(w.get(0)).height(h.get(0)).pixels(pixels32);
                icons.position(0);
                glfwSetWindowIcon(window, icons);
                STBImage.stbi_image_free(pixels32);
                STBImage.stbi_image_free(pixels16);
            }
        }
    }

    @Override
    public void loop() {
        if (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
        } else {
            ApplicationStructure.Stop();
        }
    }

    @Override
    public void show() {
        glfwShowWindow(window);
    }

    @Override
    public long getWHandle() {
        return this.window;
    }

    @Override
    public void destroy() {
        cba.free();
        Callbacks.glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
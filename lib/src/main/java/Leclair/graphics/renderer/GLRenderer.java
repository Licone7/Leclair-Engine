package Leclair.graphics.renderer;

import static org.lwjgl.opengl.GL33C.*;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;
import Leclair.window.WindowInfo;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.WGL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.windows.GDI32;
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR;
import org.lwjgl.system.windows.User32;

/**
 * @since v1
 * @author Kane Burnett
 */
public class GLRenderer implements GraphicsRenderer {

    // Windows only variables
    public static long hdc;
    static long hglrc;

    static ViewPort viewPort;
    static GLCapabilities capabilities;

    public GLRenderer(final ViewPort vp) {
        viewPort = vp;
    }

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            switch (Platform.get()) {
                case WINDOWS:
                    hdc = User32.GetDC(WindowInfo.getNativeWindow());
                    PIXELFORMATDESCRIPTOR ppfd = PIXELFORMATDESCRIPTOR.calloc(stack);
                    ppfd.nSize((short) PIXELFORMATDESCRIPTOR.SIZEOF);
                    ppfd.nVersion((short) 1);
                    ppfd.dwFlags(GDI32.PFD_DRAW_TO_WINDOW | GDI32.PFD_SUPPORT_OPENGL |
                            GDI32.PFD_DOUBLEBUFFER);
                    ppfd.dwLayerMask(GDI32.PFD_MAIN_PLANE);
                    int pixelformat = GDI32.ChoosePixelFormat(hdc, ppfd);
                    if ((pixelformat = GDI32.ChoosePixelFormat(hdc, ppfd)) == 0) {
                        throw new IllegalStateException();
                    }
                    if (GDI32.SetPixelFormat(hdc, pixelformat, ppfd) == false) {
                        throw new IllegalStateException();
                    }
                    hglrc = WGL.wglCreateContext(hdc);
                    WGL.wglMakeCurrent(hdc, hglrc);
                    break;
                case MACOSX:
                    throw new IllegalStateException("OpenGL is deprecated on MacOS. Use the Vulkan renderer!");
                case LINUX:
                    // TODO
                    break;
            }
            capabilities = GL.createCapabilities(true, MemoryUtil::memCallocPointer);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_STENCIL_TEST);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
    }

    @Override
    public void printCapabilities() {
        if (!capabilities.GL_ARB_compute_shader) { // REQUIRED
            throw new IllegalStateException();
        }
        final String api = "API: OpenGL " + glGetString(GL_VERSION);
        final String renderer = "Renderer: " + glGetString(GL_RENDERER);
        System.out.println("Graphics Info:");
        System.out.println(api);
        System.out.println(renderer);
        System.out.println("_____");
    }

    @Override
    public void loop() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, WindowInfo.getWidth(), WindowInfo.getHeight());
        switch (Platform.get()) {
            case WINDOWS:
                GDI32.SwapBuffers(hdc);
                break;
            case MACOSX:
                // TODO
                break;
            case LINUX:
                // TODO
                break;
        }
    }

    @Override
    public void setBackgroundColor(final Color backgroundColor) {
        glClearColor(backgroundColor.getR(), backgroundColor.getG(), backgroundColor.getB(), backgroundColor.getA());
    }

    @Override
    public void processMesh(final Mesh mesh) {

    }

    @Override
    public void renderMesh(final Mesh mesh) {

    }

    @Override
    public void removeMesh(final Mesh mesh) {

    }

    @Override
    public void deleteMesh(final Mesh mesh) {

    }

    @Override
    public void setWireframe(final boolean enabled) {

    }

    @Override
    public void addShader(final Shader shader, final int program, final int index) {

    }

    @Override
    public void deleteShader(final Shader shader) {

    }

    @Override
    public void cleanup() {
        GL.setCapabilities(null);
        MemoryUtil.memFree(capabilities.getAddressBuffer());
    }
}
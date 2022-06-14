package Leclair.graphics.renderer;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import Leclair.asset.AssetLoader;
import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.RenderStates;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.graphics.shader.Shaders;
import Leclair.logger.LogTypes;
import Leclair.logger.Logger;
import Leclair.math.Color;
import Leclair.window.WindowInfo;

import org.lwjgl.PointerBuffer;
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
    // Viewport
    static ViewPort viewPort;
    // OpenGL variables
    static GLCapabilities capabilities;
    // Lists
    static List<Integer> vaos;
    static List<Integer> programs;
    static List<Integer> vertShaders;
    static List<Integer> fragShaders;
    static List<Integer> textures;

    static int viewProjMatrixLocation;
    static int transformationMatrixLocation;

    public GLRenderer(final ViewPort vp) {
        viewPort = vp;
        vaos = new ArrayList<>();
        programs = new ArrayList<>();
        vertShaders = new ArrayList<>();
        fragShaders = new ArrayList<>();
        textures = new ArrayList<>();
    }

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            switch (Platform.get()) {
                case WINDOWS:
                    hdc = User32.GetDC(WindowInfo.getNativeWindow());
                    final PIXELFORMATDESCRIPTOR ppfd = PIXELFORMATDESCRIPTOR.calloc(stack);
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
        final String api = "API: OpenGL " + glGetString(GL_VERSION);
        final String renderer = "Renderer: " + glGetString(GL_RENDERER);
        Logger.getLogger().log("Graphics Capabilities:", LogTypes.TYPE_INFO);
        Logger.getLogger().log(api, LogTypes.TYPE_INFO);
        Logger.getLogger().log(renderer, LogTypes.TYPE_INFO);
        Logger.getLogger().log("_____", LogTypes.TYPE_INFO);
    }

    @Override
    public void loop() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            for (final Mesh mesh : Mesh.getMeshes()) {
                if (mesh.getState() == RenderStates.STATE_RENDER) {
                    // Shaders
                    glUseProgram(programs.get(mesh.index));
                    viewProjMatrixLocation = glGetUniformLocation(programs.get(mesh.index), "viewProjectionMatrix");
                    glUniformMatrix4fv(viewProjMatrixLocation, false,
                            viewPort.getCamera().getProjectionMatrix().get(stack.mallocFloat(16)));
                    transformationMatrixLocation = glGetUniformLocation(programs.get(mesh.index),
                            "transformationMatrix");
                    glUniformMatrix4fv(transformationMatrixLocation, false, mesh.transMat.get(stack.mallocFloat(16)));
                    // Draw arrays
                    glBindVertexArray(vaos.get(mesh.index));
                    glEnableVertexAttribArray(0);
                    glBindTexture(GL_TEXTURE_2D, textures.get(mesh.index));
                    glDrawArrays(GL_TRIANGLES, 0, mesh.getVertexNumber());
                    glBindTexture(GL_TEXTURE_2D, 0);
                    glDisableVertexAttribArray(0);
                    glBindVertexArray(0);
                }
            }
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
    }

    @Override
    public void setBackgroundColor(final Color backgroundColor) {
        glClearColor(backgroundColor.getR(), backgroundColor.getG(), backgroundColor.getB(), backgroundColor.getA());
    }

    @Override
    public void processMesh(final Mesh mesh) {
        // Generate textures
        final int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, mesh.material.getTexture().getWidth(),
                mesh.material.getTexture().getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
                mesh.material.getTexture().getData());
        glBindTexture(GL_TEXTURE_2D, 0);
        textures.add(mesh.index, id);
        // Process shaders
        final int program = glCreateProgram();
        addShader(mesh.material.getVertexShader(), program, Mesh.meshes.indexOf(mesh));
        addShader(mesh.material.getFragmentShader(), program, Mesh.meshes.indexOf(mesh));
        programs.add(mesh.index, program);
        glLinkProgram(programs.get(mesh.index));
        final int linked = glGetProgrami(programs.get(mesh.index), GL_LINK_STATUS);
        final String programLog = glGetProgramInfoLog(programs.get(mesh.index));
        if (programLog.trim().length() > 0)
            System.err.println(programLog);
        if (linked == 0)
            throw new AssertionError("Could not link program");
        glUseProgram(programs.get(mesh.index));
        // Generate vertex arrays
        final int vao = glGenVertexArrays();
        vaos.add(mesh.index, vao);
    }

    @Override
    public void renderMesh(final Mesh mesh) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final int texLocation = glGetUniformLocation(programs.get(mesh.index), "tex");
            glUniform1i(texLocation, 0);
            final int inputPosition = glGetAttribLocation(programs.get(mesh.index), "position");
            final int inputTextureCoords = glGetAttribLocation(programs.get(mesh.index), "inTexCoords");

            glUseProgram(0);

            final int vao = vaos.get(mesh.index);
            glBindVertexArray(vao);
            final int positionVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
            glBufferData(GL_ARRAY_BUFFER, mesh.getData(), GL_STATIC_DRAW);
            glVertexAttribPointer(inputPosition, 4, GL_FLOAT, false, 0, 0L);
            glEnableVertexAttribArray(inputPosition);

            final int texCoordsVbo = glGenBuffers();
            final FloatBuffer fb = stack.mallocFloat(2 * 6);
            fb.put(0.0f).put(1.0f);
            fb.put(1.0f).put(1.0f);
            fb.put(1.0f).put(0.0f);
            fb.put(1.0f).put(0.0f);
            fb.put(0.0f).put(0.0f);
            fb.put(0.0f).put(1.0f);
            fb.flip();
            glBindBuffer(GL_ARRAY_BUFFER, texCoordsVbo);
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
            glVertexAttribPointer(inputTextureCoords, 2, GL_FLOAT, true, 0, 0L);
            glEnableVertexAttribArray(inputTextureCoords);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    @Override
    public void removeMesh(final Mesh mesh) {

    }

    @Override
    public void deleteMesh(final Mesh mesh) {

    }

    @Override
    public void setWireframe(final boolean enabled) {
        if (enabled == true) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
    }

    @Override
    public void addShader(final Shader shader, final int program, final int index) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final PointerBuffer strings = stack.mallocPointer(1);
            final IntBuffer lengths = stack.mallocInt(1);
            switch (shader.shaderType) {
                case Shaders.VERTEX_SHADER:
                    final int vshader = glCreateShader(GL_VERTEX_SHADER);
                    ByteBuffer source = null;
                    source = AssetLoader.importAsBinary(shader.filePath, 8192);
                    strings.put(0, source);
                    lengths.put(0, source.remaining());
                    glShaderSource(vshader, strings, lengths);
                    glCompileShader(vshader);
                    glAttachShader(program, vshader);
                    vertShaders.add(index, vshader);
                    strings.clear();
                    lengths.clear();
                    break;
                case Shaders.FRAGMENT_SHADER:
                    final int fshader = glCreateShader(GL_FRAGMENT_SHADER);
                    source = AssetLoader.importAsBinary(shader.filePath, 8192);
                    strings.put(0, source);
                    lengths.put(0, source.remaining());
                    glShaderSource(fshader, strings, lengths);
                    glCompileShader(fshader);
                    glAttachShader(program, fshader);
                    fragShaders.add(index, fshader);
                    strings.clear();
                    lengths.clear();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid shader!");
            }
        }
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

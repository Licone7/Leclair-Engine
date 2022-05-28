package Leclair.graphics.renderer;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import Leclair.asset.AssetLoader;
import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.graphics.shader.Shaders;
import Leclair.input.Input;
import Leclair.input.InputData;
import Leclair.math.Color;
import Leclair.math.Vector3;
import Leclair.window.WindowInfo;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

public class GLRenderer implements GraphicsRenderer {

    static ViewPort viewPort;
    static GLCapabilities capabilities;

    static List<Integer> vaos = new ArrayList<>();
    static List<Integer> programs = new ArrayList<>();
    static List<Integer> vertShaders = new ArrayList<>();
    static List<Integer> fragShaders = new ArrayList<>();
    static List<Integer> textures = new ArrayList<>();
    static int ubo;
    static int transWell;

    Matrix4f projectionMatrix = new Matrix4f();
    Quaternionf orientation = new Quaternionf();
    private final Vector3 position = new Vector3(0, 2, 5);
    boolean viewing;
    float mouseX;
    float mouseY;

    public GLRenderer(final ViewPort vp) {
        viewPort = vp;
    }

    @Override
    public void init() {
        position.negate();
        GLFW.glfwMakeContextCurrent(WindowInfo.getNativeWindow());
        capabilities = GL.createCapabilities(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        GLFW.glfwSetCursorPosCallback(WindowInfo.getNativeWindow(),
                (final long window2, final double xpos, final double ypos) -> {
                    if (viewing) {
                        final float deltaX = (float) xpos - mouseX;
                        final float deltaY = (float) ypos - mouseY;
                        orientation.rotateLocalX(deltaY * 0.01f).rotateLocalY(deltaX * 0.01f);
                    }
                    mouseX = (int) xpos;
                    mouseY = (int) ypos;
                });
        GLFW.glfwSetMouseButtonCallback(WindowInfo.getNativeWindow(),
                (final long window4, final int button, final int action, final int mods) -> {
                    if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                        viewing = true;
                    } else {
                        viewing = false;
                    }
                });
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
        long start = System.currentTimeMillis();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer UtilityFB2 = stack.mallocFloat(16);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            for (final Mesh mesh : Mesh.getMeshes()) {
                if (mesh.initialized == true) {
                    glUseProgram(programs.get(mesh.index));
                    glBindBufferBase(GL_UNIFORM_BUFFER, 0, ubo);
                    int uniformId = glGetUniformBlockIndex(programs.get(mesh.index), "camera");
                    glUniformBlockBinding(programs.get(mesh.index), uniformId, ubo);
                    FloatBuffer u = updateMatrices().get(UtilityFB2);
                    glBufferData(GL_UNIFORM_BUFFER, u, GL_DYNAMIC_DRAW);
                    glBufferSubData(GL_UNIFORM_BUFFER, 64, u);
                    glUnmapBuffer(GL_UNIFORM_BUFFER);
                    glBindBuffer(GL_UNIFORM_BUFFER, 0);
                    glBindBufferBase(GL_UNIFORM_BUFFER, 1, transWell);
                    int uniformId2 = glGetUniformBlockIndex(programs.get(mesh.index), "scene");
                    glUniformBlockBinding(programs.get(mesh.index), uniformId2, transWell);
                    FloatBuffer u2 = mesh.transMat.get(UtilityFB2);
                    glBufferData(GL_UNIFORM_BUFFER, u2, GL_DYNAMIC_DRAW);
                    glBufferSubData(GL_UNIFORM_BUFFER, 64, u2);
                    glUnmapBuffer(GL_UNIFORM_BUFFER);
                    glBindBuffer(GL_UNIFORM_BUFFER, 0);
                    glVertexAttrib4fv(4, UtilityFB2.put(0, mesh.material.getAmbientColor().getR())
                            .put(1, mesh.material.getAmbientColor().getG())
                            .put(2, mesh.material.getAmbientColor().getB())
                            .put(3, mesh.material.getAmbientColor().getA()));
                    glVertexAttrib4fv(6, UtilityFB2.put(0, mesh.material.getDiffuseColor().getR())
                            .put(1, mesh.material.getDiffuseColor().getG())
                            .put(2, mesh.material.getDiffuseColor().getB())
                            .put(3, mesh.material.getDiffuseColor().getA()));
                    glVertexAttrib4fv(8, UtilityFB2.put(0, mesh.material.getSpecularColor().getR())
                            .put(1, mesh.material.getSpecularColor().getG())
                            .put(2, mesh.material.getSpecularColor().getB())
                            .put(3, mesh.material.getSpecularColor().getA()));
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
            GLFW.glfwSwapBuffers(WindowInfo.getNativeWindow());
        }
        long end = System.currentTimeMillis();
    }

    Matrix4f updateMatrices() {
        if (InputData.isKeyPressed(Input.KEY_A))
            // speed = 10f;
            position.add(0.06f, 0, 0);
        if (InputData.isKeyPressed(Input.KEY_D))
            // rotateZ -= 1f;
            position.add(-0.06f, 0, 0);
        if (InputData.isKeyPressed(Input.KEY_W))
            // rotateZ -= 1f;
            position.add(0, 0, 0.06f);
        if (InputData.isKeyPressed(Input.KEY_S))
            position.add(0, 0, -0.06f);
        if (InputData.isKeyPressed(Input.KEY_Q)) {
            // rotateZ += 1f;
            position.add(0, -0.06f, 0);
        }
        if (InputData.isKeyPressed(Input.KEY_Z)) {
            position.add(0, 0.06f, 0);
            // rotateZ -= 1f;
            // position.add(orientation.positiveZ(new Vector3f()).mul(dt * speed));
        }
        orientation.z = 0;
        final AxisAngle4f dest = new AxisAngle4f();
        orientation.get(dest);
        final float x = dest.angle;
        final float y = dest.x;
        final float z = dest.y; // Dude
        final float w = dest.z;
        // System.out.println(z);
        return projectionMatrix.setPerspective((float) Math.toRadians(70), (float) 640 / 480, 0.1f, 1000.0f)
                .rotate(x, y, z, w).translate(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public void setBackgroundColor(final Color backgroundColor) {
        glClearColor(backgroundColor.getR(), backgroundColor.getG(), backgroundColor.getB(), backgroundColor.getA());
    }

    @Override
    public void addMesh(final Mesh mesh) {
        final int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, mesh.material.getTexture().getWidth(),
                mesh.material.getTexture().getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
                mesh.material.getTexture().getData());
        glBindTexture(GL_TEXTURE_2D, 0);
        textures.add(mesh.index, id);

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
        final int texLocation = glGetUniformLocation(programs.get(mesh.index), "tex");
        glUniform1i(texLocation, 0);
        final int inputPosition = glGetAttribLocation(programs.get(mesh.index), "position");
        final int inputTextureCoords = glGetAttribLocation(programs.get(mesh.index), "texCoords");
        int uboId = glGenBuffers();
        GLRenderer.ubo = uboId;

        int tra = glGenBuffers();
        transWell = tra;

        glUseProgram(0);

        final int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        final int positionVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
        glBufferData(GL_ARRAY_BUFFER, mesh.getData(), GL_STATIC_DRAW);
        glVertexAttribPointer(inputPosition, 4, GL_FLOAT, false, 0, 0L);
        glEnableVertexAttribArray(inputPosition);
        vaos.add(mesh.index, vao);
        final int texCoordsVbo = glGenBuffers();
        final FloatBuffer fb = BufferUtils.createFloatBuffer(2 * 6);
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

    @Override
    public void deleteMesh(final Mesh mesh) {

    }

    @Override
    public void setWireframe(final boolean enabled) {

    }

    @Override
    public void addShader(final Shader shader, final int program, final int index) {
        final PointerBuffer strings = BufferUtils.createPointerBuffer(1);
        final IntBuffer lengths = BufferUtils.createIntBuffer(1);
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
            case Shaders.COMPUTE_SHADER:
                break;
            default:
                throw new IllegalArgumentException("Invalid shader!");
        }
    }

    @Override
    public void deleteShader(final Shader shader) {

    }

    @Override
    public void cleanup() {
        GL.setCapabilities(null);
    }
}
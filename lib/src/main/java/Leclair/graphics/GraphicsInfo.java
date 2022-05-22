package Leclair.graphics;

import Leclair.graphics.renderer.Renderer;
import Leclair.graphics.renderer.VKRenderer;
import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;

public class GraphicsInfo {

    /**
     * Commands the renderer to disable all graphics and compute operations
     */
    public static final byte DISABLED = 0;

    /**
     * Commands the renderer to use the Vulkan API for all graphics and compute
     * operations
     */
    public static final byte VULKAN = 1;

    /**
     * Handle to a vertex shader <br>
     * <br>
     * Used to specify the shader type when creating a new Shader();
     */
    public static final byte VERTEX_SHADER = 10;

    /**
     * Handle to a fragment shader <br>
     * <br>
     * Used to specify the shader type when creating a new Shader();
     */
    public static final byte FRAGMENT_SHADER = 11;

    /**
     * Handle to a compute shader <br>
     * <br>
     * Used to specify the shader type when creating a new Shader();
     */
    public static final byte COMPUTE_SHADER = 12;

    /**
     * Handle to an unlit material <br>
     * <br>
     * Used to specify the material type when creating a new Material();
     */
    public static final byte UNLIT_MATERIAL = 20;

    /**
     * Handle to a lit material <br>
     * <br>
     * Used to specify the material type when creating a new Material();
     */
    public static final byte LIT_MATERIAL = 21;

    /**
     * Handle to a PBR lit material <br>
     * <br>
     * Used to specify the material type when creating a new Material();
     */
    public static final byte PBR_LIT_MATERIAL = 22;

     /**
     * Handle to a terrain material <br>
     * <br>
     * Used to specify the material type when creating a new Material();
     */
    public static final byte TERRAIN_MATERIAL = 23;

    public static final byte AMBIENT_LIGHT = 30;

    public static final byte DIRECTIONAL_LIGHT = 31;

    public static final byte SPOT_LIGHT = 32;

    static byte rendererApi = VULKAN;
    static Renderer renderer;
    static ViewPort viewPort;
    static boolean useVsync = false;

    /**
     * Commands the engine to use the specified renderer <br>
     * <br>
     * Can be either {@link #DISABLED} or {@link #VULKAN}, any other value will result
     * in an IllegalArgumentException
     */
    public static void setRenderer(final byte api) {
        rendererApi = api;
    }

    /**
     * Returns the renderer being used
     * 
     * @return
     */
    public static byte getRenderer() {
        return rendererApi;
    }

    /**
     * Commands the engine whether to use Vsync or not
     */
    public static void useVsync(final boolean enabled) {
        useVsync = enabled;
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void setup(final ViewPort vp) {
        viewPort = vp;
        switch (rendererApi) {
            case DISABLED:
                break;
            case VULKAN:
                renderer = new VKRenderer(viewPort);
                break;
            default:
                throw new IllegalArgumentException("The requested renderer is invalid");
        }
        renderer.init();
        renderer.printCapabilities();
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void loop() {
        for (final Mesh mesh : Mesh.getMeshes()) {
            if (mesh.initialized == false) {
                renderer.addMesh(mesh);
                mesh.initialized = true;
            }
        }
        renderer.setBackgroundColor(viewPort.getBackgroundColor());
        renderer.setWireframe(true);
        renderer.loop();
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void cleanup() {
        renderer.cleanup();
        for (Mesh mesh : Mesh.meshes) {
            mesh.material.getTexture().free();
        }
    }
}

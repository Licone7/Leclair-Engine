package Leclair.graphics;

import Leclair.graphics.renderer.BlackHoleRenderer;
import Leclair.graphics.renderer.GLRenderer;
import Leclair.graphics.renderer.GraphicsRenderer;
import Leclair.graphics.renderer.GraphicsRenderers;
import Leclair.graphics.renderer.VKRenderer;
import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;

/**
 * @since v1
 * @author Kane Burnett
 */
public class GraphicsInfo {

    static byte rendererApi = GraphicsRenderers.VULKAN;
    static GraphicsRenderer renderer;
    static ViewPort viewPort;
    static boolean useVsync = false;

    /**
     * Commands the engine to use the specified renderer <br>
     * <br>
     * Can be either {@link #DISABLED} or {@link #VULKAN}, any other value will
     * result
     * in an IllegalArgumentException
     */
    public static void setRenderer(final byte api) {
        rendererApi = api;
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
            case GraphicsRenderers.BLACK_HOLE:
                renderer = new BlackHoleRenderer();
                break;
            case GraphicsRenderers.VULKAN:
                renderer = new VKRenderer(viewPort);
                break;
            case GraphicsRenderers.OPENGL:
                renderer = new GLRenderer(viewPort);
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

    public static GraphicsRenderer getRenderer() {
        return renderer;
    }
}
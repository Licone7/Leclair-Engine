package Leclair.application;

import Leclair.audio.renderer.AudioRenderers;
import Leclair.graphics.renderer.GraphicsRenderers;

/**
 * @since v1
 * @author Kane Burnett
 */
public class ApplicationInfo {

    /**
     * The audio API that will be used for rendering, defaults to OPENAL
     */
    static byte audioAPI = AudioRenderers.OPENAL;

    /**
     * The graphics API that will be used for rendering, defaults to VULKAN
     */
    static byte graphicsAPI = GraphicsRenderers.VULKAN;

    /**
     * The initial width of the application's window upon opening
     */
    static int initialWindowWidth = 640;

    /**
     * The initial height of the application's window upon opening
     */
    static int initialWindowHeight = 480;

    /**
     * The title of the application, used for the window title
     */
    static String title = "My new game";

    /**
     * Whether physics is enabled or not
     */
    static boolean physicsEnabled = false;

    /**
     * Commands the engine to use the specified audio API for rendering <br>
     * <br>
     * Can be either {@link #DISABLED} or {@link #OPENAL}, any other value will
     * result in an IllegalArgumentException
     */
    public static void setAudioAPI(final byte api) {
        audioAPI = api;
    }

    /**
     * Commands the engine to use the specified graphics API for rendering <br>
     * <br>
     * Can be either {@link #DISABLED} or {@link #VULKAN}, any other value will
     * result
     * in an IllegalArgumentException
     */
    public static void setGraphicsRenderer(final byte api) {
        graphicsAPI = api;
    }

    public static void setInitialWindowWidth(final int width) {
        initialWindowWidth = width;
    }

    public static void setInitialWindowHeight(final int height) {
        initialWindowHeight = height;
    }

    public static void setTitle(final String t) { // Variable is "t" since "title" is the class level variable
        title = t;
    }

    public static byte getAudioAPI() {
        return audioAPI;
    }

    public static byte getgraphicsAPI() {
        return graphicsAPI;
    }

    public static int getInitialWindowWidth() {
        return initialWindowWidth;
    }

    public static int getInitialWindowHeight() {
        return initialWindowHeight;
    }

    public static String getTitle() {
        return title;
    }

    public static boolean isPhysicsEnabled() {
        return physicsEnabled;
    }

}

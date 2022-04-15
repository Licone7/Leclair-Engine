package Leclair.application;

import Leclair.audio.AudioInfo;
import Leclair.graphics.GraphicsInfo;

/**
 * @since v1
 * @author Brett Burnett
 */
public class ApplicationInfo {

    /**
     * Handle to the engine's default settings
     * 
     */
    public static final byte RUN_BEST_PERFORMANCE = 100;

    /**
     * Handle
     */
    public static final byte RUN_BALANCED_PERFORMANCE = 101;

    /**
     * 
     */
    public static final byte RUN_BEST_GRAPHICS = 102;

    static byte graphicsApi = GraphicsInfo.OPENGL;

    static byte audioApi = AudioInfo.OPENAL;

    static int width = 640;

    static int height = 480;

    static String title = "My new game";

    static boolean vsync = false;

    static boolean physicsEnabled = false;

    /**
     * Initializes the application's information with the specified default settings
     * 
     * @param defaultInfo
     */
    public static void useDefaults(final byte defaultInfo) {
        switch (defaultInfo) {
        case RUN_BALANCED_PERFORMANCE:
            graphicsApi = GraphicsInfo.OPENGL;
            break;
        case RUN_BEST_GRAPHICS:

            break;
        }
    }
}
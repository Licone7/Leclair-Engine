package Leclair.window;

import Leclair.window.glfw.GlfwWindow;

import org.lwjgl.system.Platform;

/**
 * @since v1
 * @author Kane Burnett
 */
public class WindowInfo {

    static int width = 640;
    static int height = 480;
    static String title = "My Awesome Game";
    static String pathToIcons = null;
    static double mousePosX = 0;
    static double mousePosY = 0;

    static Window window;

    public static Window window() {
        return window;
    }

    public static int getWidth() {
        return width;
    }

    public static void setWidth(final int width) {
        WindowInfo.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(final int height) {
        WindowInfo.height = height;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(final String title) {
        WindowInfo.title = title;
    }

    public static String getPathToIcons() {
        return pathToIcons;
    }

    public static void setPathToIcons(final String pathToIcons) {
        WindowInfo.pathToIcons = pathToIcons;
    }

    public static double getMousePosX() {
        return mousePosX;
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void setMousePosX(final double mousePosX) {
        WindowInfo.mousePosX = mousePosX;
    }

    public static double getMousePosY() {
        return mousePosY;
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void setMousePosY(final double mousePosY) {
        WindowInfo.mousePosY = mousePosY;
    }

    public static long getNativeWindow() {
        return window.getWHandle();
    }

    public static void showWindow() {
        window.show();
    }

    public static void hideWindow() {

    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void setup() {
        switch (Platform.get()) {
            case WINDOWS:
                window = new GlfwWindow();
                break;
            case MACOSX:
                break;
            case LINUX:
                break;
            default:
                break;
        }
        window.init();
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void loop() {
        window.loop();
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void cleanup() {
        window.destroy();
    }
}
package Leclair.application;

import Leclair.asset.AssetLoader;
import Leclair.audio.renderer.AudioRenderer;
import Leclair.audio.renderer.AudioRenderers;
import Leclair.audio.sound.Sound;
import Leclair.audio.renderer.ALRenderer;
import Leclair.audio.renderer.AudioBlackHole;
import Leclair.graphics.renderer.GLRenderer;
import Leclair.graphics.renderer.GraphicsBlackHole;
import Leclair.graphics.renderer.GraphicsRenderer;
import Leclair.graphics.renderer.GraphicsRenderers;
import Leclair.graphics.renderer.VKRenderer;
import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.logger.LogTypes;
import Leclair.logger.Logger;
import Leclair.window.Window;
import Leclair.window.win32.Win32Window;

import org.lwjgl.system.Configuration;
import org.lwjgl.system.Platform;

/**
 * The default class to be extended when creating a game or any application with
 * Leclair Engine.
 * 
 * @since v1
 * @author Kane Burnett
 */
public abstract class ApplicationStructure {

    public static boolean RUNNING = false;
    public static Window window;
    public static ViewPort viewPort;
    public static AudioRenderer audioRenderer;
    public static GraphicsRenderer graphicsRenderer;

    public void start() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY); // We need to add multithreading
        Configuration.DISABLE_CHECKS.set(false); // Disable LWJGL checks
        AssetLoader.setup();
        // Create New Window
        switch (Platform.get()) {
            case WINDOWS:
                window = new Win32Window();
                break;
            case MACOSX:
                break;
            case LINUX:
                break;
            default:
                break;
        }
        window.init();
        // Create ViewPort
        viewPort = new ViewPort();
        // Create Graphics Renderer
        switch (ApplicationInfo.getgraphicsAPI()) {
            case GraphicsRenderers.BLACK_HOLE:
                graphicsRenderer = new GraphicsBlackHole();
                break;
            case GraphicsRenderers.VULKAN:
                graphicsRenderer = new VKRenderer(window, viewPort);
                break;
            case GraphicsRenderers.OPENGL:
                graphicsRenderer = new GLRenderer(window, viewPort);
                break;
            default:
                throw new IllegalArgumentException("The requested renderer is invalid");
        }
        graphicsRenderer.init();
        graphicsRenderer.printCapabilities();
        // Create Audio Renderer
        switch (ApplicationInfo.getAudioAPI()) {
            case AudioRenderers.BLACK_HOLE:
                audioRenderer = new AudioBlackHole();
                break;
            case AudioRenderers.OPENAL:
                audioRenderer = new ALRenderer();
                break;
            default:
                throw new IllegalArgumentException("The requested renderer is invalid");
        }
        audioRenderer.init();
        audioRenderer.printCapabilities();
        // Setup Application With User Code
        appSetup();
        window.show(); // This perhaps should be made optional.
        // The reason we don't show the window immediately is because the window freezes
        // while the graphics renderer is setting up. This doesn't happen if we wait
        // until afterwards to show the window. There might be a better way of doing
        // this though.
        loop();
    }

    /*
     * Private to prevent the user from calling this method
     */
    private void loop() {
        RUNNING = true;
        while (RUNNING == true) {
            window.loop();
            // WindowInfo.loop();
            audioRenderer.loop();
            viewPort.getCamera().update();
            graphicsRenderer.loop();
            appLoop();
        }
        cleanup();
    }

    public static void stop() {
        RUNNING = false;
    }

    /*
     * Private to prevent the user from calling this method
     */
    private void cleanup() {
        for (final Sound sound : AudioRenderer.sounds) {
            audioRenderer.deleteSound(sound);
        }
        audioRenderer.cleanup();
        graphicsRenderer.cleanup();
        for (Mesh mesh : Mesh.meshes) {
            mesh.material.getTexture().free();
        }
        window.destroy();
        appCleanup();
        Logger.getLogger().log("Engine Shutdown Complete!", LogTypes.TYPE_INFO);
        System.exit(0);
    }

    public abstract void appSetup();

    public abstract void appLoop();

    public abstract void appCleanup();

    public final class WindowManager {

    }
}

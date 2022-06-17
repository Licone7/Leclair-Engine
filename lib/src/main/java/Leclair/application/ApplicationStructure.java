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
    // The following four variables are used chiefly in the InternalsManager class
    // but for now we'll leave them class level in ApplicationStructure
    public static Window window;
    public static ViewPort viewPort;
    public static AudioRenderer audioRenderer;
    public static GraphicsRenderer graphicsRenderer;

    public void start() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY); // We need to add multithreading
        Configuration.DISABLE_CHECKS.set(false); // Disable LWJGL checks
        InternalsManager.initInternals();
        // Setup Application With User Code
        appSetup();
        loop();
    }

    /*
     * Private to prevent the user from calling this method
     */
    private void loop() {
        RUNNING = true;
        while (RUNNING == true) {
            InternalsManager.internalsLoop();
            appLoop();
            window.show(); // Prevents the window from freezing as the graphics renderer sets up, should be
                           // in the InternalsManager class but for now we can leave it here
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
        InternalsManager.internalsCleanup();
        appCleanup();
        Logger.getLogger().log("Engine Shutdown Complete!", LogTypes.TYPE_INFO);
        System.exit(0);
    }

    public abstract void appSetup();

    public abstract void appLoop();

    public abstract void appCleanup();

    private final class InternalsManager {

        private static void initInternals() {
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
        }

        public static void internalsLoop() {
            window.loop();
            audioRenderer.loop();
            viewPort.getCamera().update();
            graphicsRenderer.loop();
        }

        public static void internalsCleanup() {
            for (final Sound sound : AudioRenderer.sounds) {
                audioRenderer.deleteSound(sound);
            }
            audioRenderer.cleanup();
            graphicsRenderer.cleanup();
            for (Mesh mesh : Mesh.meshes) {
                mesh.material.getTexture().free();
            }
            window.destroy();
        }
    }

}

package Leclair.application;

import Leclair.asset.AssetLoader;
import Leclair.audio.AudioInfo;
import Leclair.graphics.GraphicsInfo;
import Leclair.graphics.scene.ViewPort;
import Leclair.window.WindowInfo;
import org.lwjgl.system.Configuration;

/**
 * The default class to be extended when creating a game or any application with
 * Leclair Engine.
 * 
 * @since v1
 * @author Brett Burnett
 */
public abstract class ApplicationStructure {

    static boolean RUNNING = false;
    public ViewPort viewPort;
    float targetFps = 60;
    float actualFps = 1000000000 / targetFps;

    public void start() {
        // long start = System.currentTimeMillis();
        // long end = System.currentTimeMillis();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Configuration.DISABLE_CHECKS.set(false);
        AssetLoader.setup();
        WindowInfo.setup();
        AudioInfo.setup();
        viewPort = new ViewPort();
        GraphicsInfo.setup(viewPort);
        appSetup();
        // System.out.println((end - start) + "ms");
        WindowInfo.showWindow();
        loop();
    }

    public void loop() {
        RUNNING = true;
        long t = System.nanoTime();
        while (RUNNING == true) {
            if (System.nanoTime() - t > actualFps) {
                WindowInfo.loop();
                AudioInfo.loop();
                viewPort.getCamera().update();
                GraphicsInfo.loop();
                appLoop();
                t = System.nanoTime();
            }
        }
        cleanup();
    }

    public static void stop() {
        RUNNING = false;
    }

    public void cleanup() {
        AudioInfo.cleanup();
        GraphicsInfo.cleanup();
        WindowInfo.cleanup();
        appCleanup();
        System.exit(0);
    }

    public abstract void appSetup();

    public abstract void appLoop();

    public abstract void appCleanup();
}

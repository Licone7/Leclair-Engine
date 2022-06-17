package Leclair.application;

import Leclair.asset.AssetLoader;
import Leclair.audio.AudioInfo;
import Leclair.graphics.GraphicsInfo;
import Leclair.graphics.scene.ViewPort;
import Leclair.logger.LogTypes;
import Leclair.logger.Logger;
import Leclair.window.WindowInfo;
import org.lwjgl.system.Configuration;

/**
 * The default class to be extended when creating a game or any application with
 * Leclair Engine.
 * 
 * @since v1
 * @author Kane Burnett
 */
public abstract class ApplicationStructure {

    public static boolean RUNNING = false;
    public ViewPort viewPort;

    public void start() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY); // We need to add multithreading
        Configuration.DISABLE_CHECKS.set(false);
        AssetLoader.setup();
        WindowInfo.setup();
        viewPort = new ViewPort();
        GraphicsInfo.setup(viewPort);
        AudioInfo.setup();
        appSetup();
        WindowInfo.showWindow(); // This perhaps should be made optional.
        // The reason we don't show the window immediately is because the window freezes
        // while the graphics renderer is setting up. This doesn't happen if we wait
        // until afterwards to show the window. There might be a better way of doing
        // this though.
        loop();
    }

    public void loop() {
        RUNNING = true;
        while (RUNNING == true) {
                WindowInfo.loop();
                AudioInfo.loop();
                viewPort.getCamera().update();
                GraphicsInfo.loop();
                appLoop();
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
        Logger.getLogger().log("Engine Shutdown Complete!", LogTypes.TYPE_INFO);
        System.exit(0);
    }

    public abstract void appSetup();

    public abstract void appLoop();

    public abstract void appCleanup();
}

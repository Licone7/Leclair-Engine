package Leclair.demos;

import org.lwjgl.system.Configuration;

import Leclair.application.ApplicationStructure;
import Leclair.audio.AudioInfo;
import Leclair.audio.effect.Effect;
import Leclair.audio.effect.Effects;
import Leclair.audio.renderer.AudioRenderers;
import Leclair.audio.sound.Sound;
import Leclair.graphics.GraphicsInfo;
import Leclair.graphics.renderer.GraphicsRenderers;
import Leclair.graphics.scene.Mesh;
import Leclair.input.key.KeyHandler;
import Leclair.input.key.Keys;
import Leclair.math.Colors;
import Leclair.math.MathUtilities;
import Leclair.window.WindowInfo;

/**
 * @since v1
 * @author Kane Burnett
 */
public class Main extends ApplicationStructure {

  Sound theme = new Sound("sounds/test.ogg", false);
  Mesh mesh3;

  public static void main(String[] args) {
    Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
    testSpeed();
    AudioInfo.setRenderer(AudioRenderers.OPENAL);
    GraphicsInfo.setRenderer(GraphicsRenderers.OPENGL);
    WindowInfo.setWidth(640);
    WindowInfo.setHeight(480);
    WindowInfo.setTitle("Leclair Engine Demo");
    Main app = new Main();

    app.start();
  }

  @Override
  public void appSetup() {
    theme.process();
    Effect effect = new Effect(Effects.FLANGER_EFFECT);
    theme.addEffect(effect);
    theme.play();
    viewPort.setBackgroundColor(Colors.RED);
    System.out.println(MathUtilities.generateRandom());
  }

  static void testSpeed() {
    // long start = System.currentTimeMillis();
    // long end = System.currentTimeMillis();
    // System.out.println((end - start) + "ms");
    // Sound s = new Sound("sounds/.ogg");
    // s.Play();
  }

  @Override
  public void appLoop() {
    if (KeyHandler.isKeyPressed(Keys.KEY_A)) {
      viewPort.setBackgroundColor(Colors.BLACK);
    } else if (KeyHandler.isKeyPressed(Keys.KEY_B)) {
      theme.play();
      viewPort.setBackgroundColor(255f, 255f, 0f, 1f);
    } else if (KeyHandler.isKeyPressed(Keys.KEY_F)) {
      theme.stop();
    } else if (KeyHandler.isKeyPressed(Keys.KEY_G)) {
      theme.delete();
    }
  }

  @Override
  public void appCleanup() {
    System.out.println("Demo run complete!");
  }
}
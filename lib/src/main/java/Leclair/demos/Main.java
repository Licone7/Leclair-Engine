package Leclair.demos;

import java.nio.FloatBuffer;

import Leclair.application.ApplicationStructure;
import Leclair.audio.AudioInfo;
import Leclair.audio.effect.Effect;
import Leclair.audio.effect.Effects;
import Leclair.audio.renderer.AudioRenderers;
import Leclair.audio.sound.Sound;
import Leclair.graphics.GraphicsInfo;
import Leclair.graphics.image.Texture;
import Leclair.graphics.material.Material;
import Leclair.graphics.material.Materials;
import Leclair.graphics.renderer.GraphicsRenderers;
import Leclair.graphics.scene.Mesh;
import Leclair.input.key.KeyHandler;
import Leclair.input.key.Keys;
import Leclair.math.Color;
import Leclair.math.MathUtilities;
import Leclair.math.Vector3;
import Leclair.window.WindowInfo;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Configuration;

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
    Effect effect = new Effect(Effects.REVERB_EFFECT);
    theme.addEffect(effect);
    theme.play();
    viewPort.setBackgroundColor(Color.RED);
    System.out.println(MathUtilities.generateRandom());
    FloatBuffer fb = BufferUtils.createFloatBuffer(4 * 6);
    fb.put(-3.0f).put(-2.0f).put(0f).put(1f);
    fb.put(3.0f).put(-2.0f).put(0f).put(1f);
    fb.put(3.0f).put(2.0f).put(0f).put(1f);
    fb.put(3.0f).put(2.0f).put(0f).put(1f);
    fb.put(-3.0f).put(2.0f).put(0f).put(1f);
    fb.put(-3.0f).put(-2.0f).put(0f).put(1f);
    fb.flip();
    Color colors = new Color(1, 0, 0, 0);
    Mesh mesh = new Mesh(fb, new Material(colors, colors, new Color(0, 1, 0, 0), 0, new Texture("textures/rust.png"),
        Materials.LIT_MATERIAL), new Vector3(0, 0, 0), true);
    mesh.render();

    mesh3 = new Mesh(fb, new Material(colors, colors, new Color(0, 0, 1, 0), 0, new Texture("textures/bond.jpg"),
        Materials.LIT_MATERIAL), new Vector3(0, 5, 0), true);

    FloatBuffer fb2 = BufferUtils.createFloatBuffer(4 * 6);
    fb2.put(-1.0f).put(-1.0f).put(1f).put(1f);
    fb2.put(1.0f).put(-1.0f).put(1f).put(1f);
    fb2.put(1.0f).put(1.0f).put(1f).put(1f);
    fb2.put(1.0f).put(1.0f).put(1f).put(1f);
    fb2.put(-1.0f).put(1.0f).put(1f).put(1f);
    fb2.put(-1.0f).put(-1.0f).put(1f).put(1f);
    fb2.flip();

    Mesh mesh2 = new Mesh(fb2, new Material(colors, colors, new Color(0, 0, 0, 0), 0, new Texture("textures/bond.jpg"),
        (byte) Materials.LIT_MATERIAL), new Vector3(-8, 0, 0), false);
    mesh2.process();
    mesh2.render();
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
      viewPort.setBackgroundColor(Color.BLACK);
    } else if (KeyHandler.isKeyPressed(Keys.KEY_B)) {
      theme.play();
      mesh3.render();
      viewPort.setBackgroundColor(255f, 255f, 0f, 1f);
    } else if (KeyHandler.isKeyPressed(Keys.KEY_F)) {
      theme.stop();
    } else if (KeyHandler.isKeyPressed(Keys.KEY_G)) {
      theme.delete();
      mesh3.remove();
    }
  }

  @Override
  public void appCleanup() {
    System.out.println("Demo run complete!");
  }
}
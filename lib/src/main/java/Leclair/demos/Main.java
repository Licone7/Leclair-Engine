package Leclair.demos;

import java.nio.FloatBuffer;

import Leclair.application.ApplicationStructure;
import Leclair.audio.effect.Effect;
import Leclair.audio.effect.Effects;
import Leclair.audio.sound.Sound;
import Leclair.graphics.GraphicsInfo;
import Leclair.graphics.image.Texture;
import Leclair.graphics.material.Material;
import Leclair.graphics.material.Materials;
import Leclair.graphics.renderer.GraphicsRenderers;
import Leclair.graphics.scene.Mesh;
import Leclair.input.Input;
import Leclair.input.InputData;
import Leclair.math.Color;
import Leclair.math.MathUtilities;
import Leclair.math.Vector3;
import Leclair.window.WindowInfo;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Configuration;

public class Main extends ApplicationStructure {

  Sound theme = new Sound("sounds/test.ogg", false);

  public static void main(String[] args) {
    Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
    testSpeed();
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
    Color colors = new Color(1, 20, 10, 0);
    Mesh mesh = new Mesh(fb, new Material(colors, colors, new Color(0, 0, 10, 0), 0, new Texture("textures/rust.png"),
        Materials.LIT_MATERIAL), new Vector3(0, 0, 0));
    mesh.getVertexNumber();

    Mesh mesh3 = new Mesh(fb, new Material(colors, colors, new Color(0, 10, 10, 0), 0, new Texture("textures/rust.png"),
        Materials.LIT_MATERIAL), new Vector3(0, 5, 0));
    mesh3.getVertexNumber();

    FloatBuffer fb2 = BufferUtils.createFloatBuffer(4 * 6);
    fb2.put(-1.0f).put(-1.0f).put(1f).put(1f);
    fb2.put(1.0f).put(-1.0f).put(1f).put(1f);
    fb2.put(1.0f).put(1.0f).put(1f).put(1f);
    fb2.put(1.0f).put(1.0f).put(1f).put(1f);
    fb2.put(-1.0f).put(1.0f).put(1f).put(1f);
    fb2.put(-1.0f).put(-1.0f).put(1f).put(1f);
    fb2.flip();
    Mesh mesh2 = new Mesh(fb2, new Material(colors, colors, new Color(0, 0, 10, 0), 0, new Texture("textures/bond.jpg"),
        (byte) Materials.LIT_MATERIAL), new Vector3(-8, 0, 0));
    mesh2.getVertexNumber();

    // Mesh mesh3 = new Mesh(fb2, new Material(new Texture("textures/.PNG")), new
    // Vector3(0, 3, 0));
    // mesh3.getVertexNumber();
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
    if (InputData.isKeyPressed(Input.KEY_A)) {
      viewPort.setBackgroundColor(Color.BLACK);
    } else if (InputData.isKeyPressed(Input.KEY_B)) {
      theme.play();
      viewPort.setBackgroundColor(255f, 255f, 0f, 1f);
    } else if (InputData.isKeyPressed(Input.KEY_D)) {
      // theme.stop();
    } else if (InputData.isKeyPressed(Input.KEY_C)) {
      theme.delete();
    }
  }

  @Override
  public void appCleanup() {
    System.out.println("Window Destroyed");
  }
}
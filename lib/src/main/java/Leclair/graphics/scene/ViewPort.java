package Leclair.graphics.scene;

import static Leclair.application.ApplicationStructure.graphicsRenderer;

import Leclair.math.Color;
import Leclair.math.Colors;

/**
 * 
 * @since v1
 * @author Kane Burnett
 */
public class ViewPort {

    Camera camera;
    Color color = Colors.WHITE;

    public ViewPort() {
        camera = new Camera();
    }

    public void setBackgroundColor(final float r, final float g, final float b, final float a) {
        // this.color = new Color(r, g, b, a);
        this.color.set(r, g, b, a);
        graphicsRenderer.setBackgroundColor(this.color);
    }

    public void setBackgroundColor(final Color value) {
        setBackgroundColor(value.getR(), value.getG(), value.getB(), value.getA());
    }

    public Color getBackgroundColor() {
        return color;
    }

    public Camera getCamera() {
        return this.camera;
    }
}

package Leclair.graphics.scene;

import Leclair.math.Color;

/**
 * 
 * @since v1
 * @author Brett Burnett
 */
public class ViewPort {

    Camera camera;
    Color color = Color.WHITE;

    public ViewPort() {
        camera = new Camera();
    }

    public void setBackgroundColor(final float r, final float g, final float b, final float a) {
        this.color = new Color(r, g, b, a);
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

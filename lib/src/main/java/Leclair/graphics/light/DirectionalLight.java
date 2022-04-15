package Leclair.graphics.light;

import Leclair.graphics.GraphicsInfo;
import Leclair.math.Color;
import Leclair.math.Vector3;

public class DirectionalLight implements Light {

    public static final byte type = GraphicsInfo.DIRECTIONAL_LIGHT;
    Color color;
    Vector3 direction;
    float intensity;

    public DirectionalLight(final Color color, final Vector3 direction, final float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

}

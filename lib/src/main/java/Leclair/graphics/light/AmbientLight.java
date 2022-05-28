package Leclair.graphics.light;

import Leclair.math.Color;

public class AmbientLight implements Light {

    public static final byte type = Lights.AMBIENT_LIGHT;
    Color color;

    public AmbientLight(Color color) {
        this.color = color;
    }
}

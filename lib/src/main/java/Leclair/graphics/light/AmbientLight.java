package Leclair.graphics.light;

import Leclair.graphics.GraphicsInfo;
import Leclair.math.Color;

public class AmbientLight implements Light {

    public static final byte type = GraphicsInfo.AMBIENT_LIGHT;
    Color color;

    public AmbientLight(Color color) {
        this.color = color;
    }
}

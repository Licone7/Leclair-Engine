package Leclair.graphics.light;

import Leclair.math.Color;

/**
 * @since v1
 * @author Kane Burnett
 */
public class AmbientLight implements Light {

    public static final byte type = Lights.AMBIENT_LIGHT;
    Color color;

    public AmbientLight(Color color) {
        this.color = color;
    }
}

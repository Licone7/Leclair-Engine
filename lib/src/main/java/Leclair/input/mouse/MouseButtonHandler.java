package Leclair.input.mouse;

/**
 * @since v1
 * @author Kane Burnett
 */
public class MouseButtonHandler {
    public static boolean MOUSE_BUTTON_PRESS = false;
    public static byte MOUSE_BUTTON = 0;

    public static boolean isMouseButtonPressed(byte mouseButton) {
        if (MOUSE_BUTTON_PRESS == true) {
            switch (MOUSE_BUTTON) {
                case MouseButtons.MOUSE_BUTTON_LEFT:
                    System.out.print("L");
                    return true;
                case MouseButtons.MOUSE_BUTTON_RIGHT:
                    System.out.print("R");
                    return true;
                case MouseButtons.MOUSE_BUTTON_MIDDLE:
                    System.out.print("M");
                    return true;
            }
        }
        return false;
    }
}
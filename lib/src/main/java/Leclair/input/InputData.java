package Leclair.input;

public class InputData {

    public static boolean KEY_DOWN = false;
    public static char CHAR = '\u0000';

    public static boolean isKeyPressed(byte key) {
        if (KEY_DOWN == true) {
            switch (CHAR) {
                case 'A':
                    if (key == Input.KEY_A) {
                        return true;
                    }
                    break;
                case 'B':
                    if (key == Input.KEY_B) {
                        return true;
                    }
                    break;
                case 'C':
                    if (key == Input.KEY_C) {
                        return true;
                    }
                case 'D':
                    if (key == Input.KEY_D) {
                        return true;
                    }
                    break;
                    case 'W':
                    if (key == Input.KEY_W) {
                        return true;
                    }
                    break;
                    case 'S':
                    if (key == Input.KEY_S) {
                        return true;
                    }
                    break;
                    case 'Q':
                    if (key == Input.KEY_Q) {
                        return true;
                    }
                    break;
                    case 'Z':
                    if (key == Input.KEY_Z) {
                        return true;
                    }
                    break;

                default:
            }
        }
        return false;
    }
}

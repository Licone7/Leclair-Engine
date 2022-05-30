package Leclair.input;

import Leclair.input.key.Keys;

public class InputData {

    public static boolean KEY_DOWN = false;
    public static char CHAR = '\u0000';

    public static boolean isKeyPressed(byte key) {
        if (KEY_DOWN == true) {
            switch (CHAR) {
                case 'A':
                    if (key == Keys.KEY_A) {
                        return true;
                    }
                    break;
                case 'B':
                    if (key == Keys.KEY_B) {
                        return true;
                    }
                    break;
                case 'C':
                    if (key == Keys.KEY_C) {
                        return true;
                    }
                    break;
                case 'D':
                    if (key == Keys.KEY_D) {
                        return true;
                    }
                    break;
                case 'E':
                    if (key == Keys.KEY_E) {
                        return true;
                    }
                    break;
                case 'F':
                    if (key == Keys.KEY_F) {
                        return true;
                    }
                    break;
                case 'G':
                    if (key == Keys.KEY_G) {
                        return true;
                    }
                    break;
                case 'H':
                    if (key == Keys.KEY_H) {
                        return true;
                    }
                    break;
                case 'I':
                    if (key == Keys.KEY_I) {
                        return true;
                    }
                    break;
                case 'J':
                    if (key == Keys.KEY_J) {
                        return true;
                    }
                    break;
                case 'K':
                    if (key == Keys.KEY_K) {
                        return true;
                    }
                    break;
                case 'L':
                    if (key == Keys.KEY_L) {
                        return true;
                    }
                    break;
                case 'M':
                    if (key == Keys.KEY_M) {
                        return true;
                    }
                    break;
                case 'N':
                    if (key == Keys.KEY_N) {
                        return true;
                    }
                    break;
                case 'O':
                    if (key == Keys.KEY_O) {
                        return true;
                    }
                    break;
                case 'P':
                    if (key == Keys.KEY_P) {
                        return true;
                    }
                    break;
                case 'Q':
                    if (key == Keys.KEY_Q) {
                        return true;
                    }
                    break;
                case 'R':
                    if (key == Keys.KEY_R) {
                        return true;
                    }
                    break;
                case 'S':
                    if (key == Keys.KEY_S) {
                        return true;
                    }
                    break;
                case 'T':
                    if (key == Keys.KEY_T) {
                        return true;
                    }
                    break;
                case 'U':
                    if (key == Keys.KEY_U) {
                        return true;
                    }
                    break;
                case 'V':
                    if (key == Keys.KEY_V) {
                        return true;
                    }
                    break;
                case 'W':
                    if (key == Keys.KEY_W) {
                        return true;
                    }
                    break;
                case 'X':
                    if (key == Keys.KEY_X) {
                        return true;
                    }
                    break;
                case 'Y':
                    if (key == Keys.KEY_Y) {
                        return true;
                    }
                    break;
                case 'Z':
                    if (key == Keys.KEY_Z) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
}

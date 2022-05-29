package Leclair.graphics.scene;

public interface RenderStates {

    public static byte STATE_UNPROCESSED = 0;
    public static byte STATE_PROCESS = 1;
    public static byte STATE_RENDER = 2;
    public static byte STATE_REMOVE = 3;
    public static byte STATE_DELETE = 4;
}
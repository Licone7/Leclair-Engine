package Leclair.window;

/**
 * @since v1
 * @author Kane Burnett
 */
public interface Window {

    public void init();

    public void loop();

    public void show();

    public long getNativeWindowHandle();

    public void destroy();

}

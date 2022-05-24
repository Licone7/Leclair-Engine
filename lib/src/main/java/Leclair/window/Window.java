package Leclair.window;

/**
 * @since v1
 * @author Brett Burnett
 */
public interface Window {

    public void init();

    public void loop();

    public void show();

    public long getWHandle();

    public void destroy();

}

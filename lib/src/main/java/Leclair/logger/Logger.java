package Leclair.logger;

/**
 * This class provides the ability to log messages, with varying levels of
 * severity
 * 
 * @author Kane Burnett
 */
public class Logger {

    static Logger logger = new Logger(); // Default logger

    /**
     * Returns the default logger, already initialized
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * The standard logging mechanism - if the given type is TYPE_ERROR, a
     * RuntimeException without a message will be thrown
     * 
     * @param message
     * @param type
     */
    public void log(final String message, final byte type) {
        switch (type) {
            case LogTypes.TYPE_INFO:
                System.out.println("[INFO] " + message);
                break;
            case LogTypes.TYPE_WARNING:
                System.err.println("[WARNING] " + message);
                break;
            case LogTypes.TYPE_ERROR:
                System.err.println("[ERROR] " + message);
                throw new RuntimeException();
        }
    }

    /**
     * This method contains a third parameter for a specific Throwable to be thrown
     * if the type is TYPE_ERROR
     * 
     * @param message
     * @param type
     * @param throwable
     */
    public void logWithThrowable(final String message, final byte type, final RuntimeException exception) {
        switch (type) {
            case LogTypes.TYPE_INFO:
                System.out.println("[INFO] " + message);
                break;
            case LogTypes.TYPE_WARNING:
                System.err.println("[WARNING] " + message);
                break;
            case LogTypes.TYPE_ERROR:
                System.err.println("[ERROR] " + message);
                throw exception;
        }
    }

}

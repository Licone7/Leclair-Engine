package Leclair.logger.exceptions;

/**
 * This exception is similar to {@code FileNotFoundException} except this class
 * extends {@code RuntimeException}.
 * 
 * @author Kane Burnett
 */
public class ResourceUnfoundException extends RuntimeException {

    public ResourceUnfoundException() {
        super();
    }

}

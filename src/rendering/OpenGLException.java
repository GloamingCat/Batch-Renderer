package rendering;

public class OpenGLException extends RuntimeException {

    public OpenGLException(String message) {
        super("On thread " + Thread.currentThread() + ": " + message);
    }

}

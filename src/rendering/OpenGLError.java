package rendering;

public class OpenGLError extends Error {

    public OpenGLError(String message) {
        super("On thread " + Thread.currentThread() + ": " + message);
    }

}

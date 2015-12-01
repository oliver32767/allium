package io.firstwave.allium.api2;

/**
 * Created by obartley on 12/1/15.
 */
public class RenderException extends RuntimeException {
    public RenderException() {
        super();
    }

    public RenderException(String message) {
        super(message);
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RenderException(Throwable cause) {
        super(cause);
    }
}

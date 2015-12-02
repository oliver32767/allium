package io.firstwave.allium.api;

/**
 * Created by obartley on 12/1/15.
 */
public class RenderException extends RuntimeException {
    public RenderException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}

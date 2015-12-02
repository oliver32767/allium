package io.firstwave.allium.api;

/**
 * Created by obartley on 12/1/15.
 */
public abstract class RenderContext {

    public final double width;
    public final double height;

    private Throwable mException;

    public RenderContext(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public abstract void publish(Layer layer);

    final void setException(Throwable exception) {
        mException = exception;
    }

    public final Throwable getException() {
        return mException;
    }
}

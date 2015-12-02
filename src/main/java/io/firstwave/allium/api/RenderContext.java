package io.firstwave.allium.api;

/**
 * Created by obartley on 12/1/15.
 */
public final class RenderContext {

    public final double width;
    public final double height;

    private final Publisher mPublisher;
    private final LogWriter mLogWriter;

    private Throwable mException;

    public RenderContext(double width, double height, Publisher publisher, LogWriter logWriter) {
        this.width = width;
        this.height = height;
        mPublisher = publisher;
        mLogWriter = logWriter;
    }

    final void publish(Layer layer) {
        mPublisher.publish(layer);
    }

    public final void log(String tag, String message) {
        mLogWriter.log(tag, message);
    }

    final void setException(Throwable exception) {
        mException = exception;
    }

    public final Throwable getException() {
        return mException;
    }

    public interface Publisher {
        void publish(Layer layer);
    }

    public interface LogWriter {
        void log(String tag, String message);
    }
}

package io.firstwave.allium.api;

import org.pmw.tinylog.Logger;

/**
 * Created by obartley on 12/1/15.
 */
public final class RenderContext {

    public final double width;
    public final double height;

    private final Publisher mPublisher;
    private final MessageHandler mMessageHandler;
    private final ExceptionHandler mExceptionHandler;

    public RenderContext(double width, double height, Publisher publisher, MessageHandler messageHandler) {
        this(width, height, publisher, messageHandler, null);
    }

    public RenderContext(double width, double height, Publisher publisher, MessageHandler messageHandler, ExceptionHandler exceptionHandler) {
        this.width = width;
        this.height = height;
        mPublisher = publisher;
        mMessageHandler = messageHandler;
        mExceptionHandler = exceptionHandler;
    }


    final void publish(Layer layer) {
        if (mPublisher != null) {
            mPublisher.publish(layer);
        }
    }

    public final void handleMessage(Object source, String message) {
        if (mMessageHandler != null) {
            mMessageHandler.log(source, message);
        } else {
            Logger.debug(source + ": " + message);
        }
    }

    final void handleException(Object source, Throwable exception) {
        if (mExceptionHandler != null) {
            mExceptionHandler.handle(source, exception);
        } else {
            Logger.warn(source + ": " + exception.getMessage());
            handleMessage(source, exception.getMessage());
        }
    }

    public interface Publisher {
        void publish(Layer layer);
    }

    public interface MessageHandler {
        void log(Object source, String message);
    }

    public interface ExceptionHandler {
        void handle(Object source, Throwable tr);
    }
}

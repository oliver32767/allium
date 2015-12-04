package io.firstwave.allium.api;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

    private final IntegerProperty mLayerCount = new SimpleIntegerProperty(0);
    private final IntegerProperty mPublishCount = new SimpleIntegerProperty(0);
    private final SimpleBooleanProperty mIsActive = new SimpleBooleanProperty(false);

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
            mPublisher.publish(this, layer);
        }
        mPublishCount.setValue(mPublishCount.getValue() + 1);
    }

    public final void handleMessage(Object source, String message) {
        if (mMessageHandler != null) {
            mMessageHandler.log(this, source, message);
        } else {
            Logger.debug(source + ": " + message);
        }
    }

    final void handleException(Object source, Throwable exception) {
        Logger.error(exception);
        if (mExceptionHandler != null) {
            mExceptionHandler.handle(this, source, exception);
        } else {
            Logger.warn(source + ": " + exception.getMessage());
            handleMessage(source, exception.getMessage());
        }
    }

    final void onPreRenderComplete(Layer layer) {
        mLayerCount.setValue(mLayerCount.getValue() + 1);
    }

    public final int getLayerCount() {
        return mLayerCount.getValue();
    }

    public final IntegerProperty layerCountProperty() {
        return mLayerCount;
    }

    public final int getPublishCount() {
        return mPublishCount.getValue();
    }

    public final IntegerProperty publishCountProperty() {
        return mPublishCount;
    }

    final void setActive(boolean active) {
        mIsActive.setValue(active);
    }

    public final BooleanProperty activeProperty() {
        return mIsActive;
    }

    @Override
    public String toString() {
        return RenderContext.class.getSimpleName() + " [" + width + " x " + height + "]";
    }

    public interface Publisher {
        void publish(RenderContext ctx, Layer layer);
    }

    public interface MessageHandler {
        void log(RenderContext ctx, Object source, String message);
    }

    public interface ExceptionHandler {
        void handle(RenderContext ctx, Object source, Throwable tr);
    }
}

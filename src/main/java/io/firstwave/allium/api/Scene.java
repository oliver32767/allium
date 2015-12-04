package io.firstwave.allium.api;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import org.pmw.tinylog.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by obartley on 12/1/15.
 */
public abstract class Scene {
    
    private Layer mRoot;

    private double mWidth = 0;
    private double mHeight = 0;
    private SimpleObjectProperty<Color> mBackgroundColor = new SimpleObjectProperty<>(Color.TRANSPARENT);

    private final SimpleBooleanProperty mIsRendering = new SimpleBooleanProperty(false);

    
    public final Layer getRoot() {
        return mRoot;
    }

    public double getWidth() {
        return mWidth;
    }

    public void setWidth(double width) {
        mWidth = width;
    }

    public double getHeight() {
        return mHeight;
    }

    public void setHeight(double height) {
        mHeight = height;
    }

    public Color getBackgroundColor() {
        return mBackgroundColor.getValue();
    }

    public void setBackgroundColor(Color color) {
        mBackgroundColor.setValue(color);
    }

    public ObservableValue<Color> backgroundColorProperty() {
        return mBackgroundColor;
    }

    public final void load() {
        mRoot = onCreate();
    }
    
    protected abstract Layer onCreate();

    public BooleanProperty renderingProperty() {
        return mIsRendering;
    }

    public final void render(final RenderContext ctx) {
        if (mIsRendering.getValue()) {
            Logger.debug("Render in progress -- skipping");
            return;
        }

        mIsRendering.setValue(true);

        final long startTime = System.currentTimeMillis();

        // pre render on main thread
        onPreRender(ctx);
        if (mRoot != null) {
            mRoot.each(layer -> layer.preRender(ctx));
        }

        ctx.handleMessage(this, "Starting rendering");
        final Task<Void> renderTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                onRender(ctx);
                if (mRoot != null) {
                    mRoot.each(layer -> layer.render(ctx));
                    mRoot.each(Layer::publish);
                }

                final float elapsed = (float) (System.currentTimeMillis() - startTime) / 1000;
                ctx.handleMessage(this, String.format("Rendered in %.4f second(s)", elapsed));
                return null;
            }

            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            protected void failed() {
                mIsRendering.setValue(false);
                final Throwable tr = getException();
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                Logger.warn(tr);
                tr.printStackTrace(pw);
                ctx.handleMessage(this, tr.getMessage());
                ctx.handleMessage(this, sw.toString());
            }

            @Override
            protected void succeeded() {
                mIsRendering.setValue(false);
            }
        };

        Thread th = new Thread(renderTask);
        th.setDaemon(true);
        th.start();
    }

    protected void onPreRender(RenderContext ctx) {

    }

    protected void onRender(RenderContext ctx) {

    }
}

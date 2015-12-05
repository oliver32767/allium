package io.firstwave.allium.api;

import javafx.beans.property.ObjectProperty;
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

    private SimpleObjectProperty<RenderContext> mRenderContext = new SimpleObjectProperty<>();

    private void setRoot(Layer root) {
        mRoot = root;
        mRoot.setScene(this);
    }

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
        setRoot(onCreate());
    }
    
    protected abstract Layer onCreate();

    /**
     * Scenes should not call this method directly, the viewer will handle this one dude
     */
    public final void render(final RenderContext ctx) {
        if (mRenderContext.getValue() != null) {
            Logger.debug("Render in progress -- skipping");
            return;
        }
        mRenderContext.setValue(ctx);

        final long startTime = System.currentTimeMillis();

        ctx.handleMessage("START", "→ Starting render with " + ctx);

        // pre render on main thread
        onPreRender(ctx);
        if (mRoot != null) {
            mRoot.each(layer -> layer.preRender(ctx));
        }

        final Task<Void> renderTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                onRender(ctx);
                if (mRoot != null) {
                    mRoot.each(layer -> layer.render(ctx));
                    mRoot.each(Layer::publish);
                }
                return null;
            }

            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            protected void failed() {
                mRenderContext.setValue(null);
                final Throwable tr = getException();
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                Logger.warn(tr);
                tr.printStackTrace(pw);
                ctx.handleMessage("FAIL", "↖ Rendering could not be completed: " + tr + "\n");
            }

            @Override
            protected void succeeded() {
                mRenderContext.setValue(null);
                final float elapsed = (float) (System.currentTimeMillis() - startTime) / 1000;
                ctx.handleMessage("FINISH", String.format("← Rendered %d/%d layer(s) in %.4f second(s)\n", ctx.getLayerCount(), ctx.getPublishCount(), elapsed));
            }
        };

        Thread th = new Thread(renderTask);
        th.setDaemon(true);
        th.start();
    }

    public final ObjectProperty<RenderContext> renderContextProperty() {
        return mRenderContext;
    }

    protected void onPreRender(RenderContext ctx) {

    }

    protected void onRender(RenderContext ctx) {

    }


}

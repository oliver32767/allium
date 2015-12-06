package io.firstwave.allium.api;

import io.firstwave.allium.api.inject.Injector;
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
     * This method, along with {@link Layer#render(RenderContext)} and {@link Layer#preRender(RenderContext)} contains
     * the logic for handling the rendering sequence. You shouldn't invoke this method directly, the viewer will call
     * this when the user has requested a render.
     *
     * The rendering sequence is broken up into two phases: pre-render and render.
     *
     * Each phase follows the same pattern, with the difference being that pre-render occurs on the main thread allowing
     * you an opportunity to modify your layer tree before the render pass begins. Once the pre-render pass is complete,
     * a worker thread will be spun up to handle the potentially long-running render pass. You can not modify your
     * layer tree during this phase. Most layers will not have an onPreRender implementation.
     *
     * The sequence is as follows:
     *
     * {@link Scene#onPreRender(RenderContext)} is called.
     *
     * beginning with the root node:
     *  call {@link Layer#preRender(RenderContext)} which:
     *      dispatches a call to {@link Layer#onPreRender(RenderContext)}
     *      calls {@link Layer#preRender(RenderContext)} on each of its children
     *
     * Each Layer has an associated state which describes its state in this rendering sequence, see {@link RenderState}
     *
     * By default, a layer will not complete a phase until all of its child layers have also completed, though
     * if you need more complex layer dependencies, you can manually trigger a preRender or render by calling the relevant
     * method on a layer yourself.
     *
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
            mRoot.preRender(ctx);
        }

        final Task<Void> renderTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                onRender(ctx);
                if (mRoot != null) {
                    mRoot.render(ctx);
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

    /**
     * Called during the scene's pre render pass. This is called in the main thread, so use this callback
     * to make any modifications you need to make to the layer tree.
     *
     * The default implementation performs an injection (see {@link Injector#inject(Object, Layer)})
     * @param ctx
     */
    protected void onPreRender(RenderContext ctx) {
        Injector.inject(this, mRoot);
    }

    protected void onRender(RenderContext ctx) {

    }


}

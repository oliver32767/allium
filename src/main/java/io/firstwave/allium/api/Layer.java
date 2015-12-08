package io.firstwave.allium.api;

import io.firstwave.allium.api.inject.Injector;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.utils.FXUtils;
import io.firstwave.allium.utils.ThreadEnforcer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;

/**
 * Created by obartley on 12/1/15.
 */
public class Layer {

    private final ObservableList<Layer> mChildNodes = FXCollections.observableArrayList();

    private Options mOptions;
    private Layer mParent;
    private Scene mScene;
    private RenderContext mRenderContext;
    private Canvas mCanvas;

    private final SimpleStringProperty mName = new SimpleStringProperty();
    private final SimpleBooleanProperty mVisible = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty mExpanded = new SimpleBooleanProperty(true);

    private final SimpleObjectProperty<RenderState> mState = new SimpleObjectProperty<>(RenderState.IDLE);
    private final SimpleStringProperty mMessage = new SimpleStringProperty();

    ThreadEnforcer mThreadEnforcer = ThreadEnforcer.MAIN;

    public Layer() {
        this(null, null);
    }

    public Layer(String name) {
        this(name, null);
    }

    public Layer(String name, Options options) {
        setName(name);
        setOptions(options);
        mVisible.addListener((observable, oldValue, newValue) -> updateChildVisibility(newValue));
    }

    public final Options getOptions() {
        return mOptions;
    }

    /**
     * Main thread only
     */
    public final void setOptions(Options configuration) {
        mThreadEnforcer.enforce();
        if (configuration == null) {
            configuration = Options.EMPTY;
        }
        mOptions = configuration;
    }


    public String getName() {
        return mName.getValue();
    }

    /**
     * Main thread only
     * @param name
     */
    public void setName(String name) {
        mThreadEnforcer.enforce();
        if (name == null) {
            name = getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
        }
        mName.setValue(name);
    }

    public final ObservableValue<String> nameProperty() {
        return mName;
    }

    public final boolean isVisible() {
        return mVisible.getValue();
    }

    /**
     * Main thread only
     * @param visible
     */
    public final void setVisible(boolean visible) {
        mThreadEnforcer.enforce();
        mVisible.setValue(visible);
    }

    private void updateChildVisibility(boolean visible) {
        for (Layer child : mChildNodes) {
            child.setVisible(visible);
        }
    }

    public final BooleanProperty visibleProperty() {
        return mVisible;
    }

    public final boolean isExpanded() {
        return mExpanded.getValue();
    }

    public final void setExpanded(boolean expanded) {
        mExpanded.setValue(expanded);
    }

    public final BooleanProperty expandedProperty() {
        return mExpanded;
    }

    public final RenderState getState() {
        return mState.getValue();
    }

    public final ObservableValue<RenderState> stateProperty() {
        return mState;
    }

    protected void setMessage(String message) {
        if (mRenderContext != null) {
            mRenderContext.handleMessage(this, message);
        }
        FXUtils.runOnMainThread(() -> mMessage.setValue(message));
    }

    public final ObservableValue<String> messageProperty() {
        return mMessage;
    }

    // CHILD NODE API /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return a read only, observable list of children
     * @return
     */
    public final ObservableList<Layer> getChildNodes() {
        return FXCollections.unmodifiableObservableList(mChildNodes);
    }

    public final int getChildCount() {
        return mChildNodes.size();
    }

    public final Layer getChildAt(int i) {
        return mChildNodes.get(i);
    }

    public final Layer findChildByName(String name) {
        if (name == null) {
            return null;
        }

        if (name.equals(mName.getValue())) {
            return this;
        }

        for (Layer child : mChildNodes) {
            child = child.findChildByName(name);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    final void setScene(Scene scene) {
        mScene = scene;
    }

    protected Scene getScene() {
        if (mScene == null && mParent != null) {
            return mParent.getScene();
        }
        return mScene;
    }

    /**
     * Main thread only
     */
    public final Layer addChild(Layer child) {
        mThreadEnforcer.enforce();
        child.removeFromParent();

        child.mParent = this;
        mChildNodes.add(child);

        return child;
    }

    /**
     * Main thread only
     */
    public final Layer addChild(int index, Layer child) {
        mThreadEnforcer.enforce();
        child.removeFromParent();

        child.mParent = this;
        child.setScene(getScene());
        mChildNodes.add(index, child);

        // if the layer is added during pre render, we need to ensure the child's
        // pre render pass is completed as well
        if (getState() == RenderState.PREPARING && mRenderContext != null) {
            child.preRender(mRenderContext);
        }

        return child;
    }

    /**
     * Main thread only
     */
    public final void removeChild(Layer child) {
        mThreadEnforcer.enforce();
        final int index = indexOf(child);
        if (index >= 0) {
            removeChildInternal(index);
        }
    }

    /**
     * Main thread only
     */
    public final void removeChildAt(int index) {
        mThreadEnforcer.enforce();
        removeChildInternal(index);
    }

    /**
     * Main thread only
     */
    public final void removeAllChildren() {
        mThreadEnforcer.enforce();
        for (int i = mChildNodes.size() - 1; i >= 0; i--) {
            removeChildInternal(i);
        }
    }

    private void removeChildInternal(int index) {
        mChildNodes.get(index).mParent = null;
        mChildNodes.get(index).mScene = null;
        mChildNodes.remove(index);
    }

    public final int indexOf(Layer child) {
        return mChildNodes.indexOf(child);
    }

    /**
     * Main thread only
     */
    public final void removeFromParent() {
        mThreadEnforcer.enforce();
        if (mParent == null) {
            return;
        }
        mParent.removeChild(this);
    }

    // RENDER API //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Prepare this layer and its children for a render pass.
     * @param ctx
     */
    public final void preRender(RenderContext ctx) {
        ThreadEnforcer.MAIN.enforce();
        if (mRenderContext != null || getState() == RenderState.PREPARING) {
            return;
        }
        mState.setValue(RenderState.PREPARING);
        mRenderContext = ctx;


        try {
            onPreRender(ctx);
            mCanvas = onCreateCanvas(ctx);
        } catch (Throwable tr) {
            ctx.handleException(this, tr);
            mCanvas = null;
            mState.setValue(RenderState.ERROR);
        }

        for (Layer child : mChildNodes) {
            child.preRender(ctx);
        }
        mState.setValue(RenderState.READY);

        ctx.onPreRenderComplete(this);
    }

    /**
     * Override to provide a custom canvas to your layer for rendering. While not required,
     * you should ensure that the returned canvas has the same dimensions as your scene.
     * Occurs during pre render
     *
     * @param ctx
     * @return
     */
    protected Canvas onCreateCanvas(RenderContext ctx) {
        return new Canvas(getScene().getWidth(), getScene().getHeight());
    }

    /**
     * Return the canvas created in this layer's pre render pass. May be null.
     * @return
     */
    public final Canvas getCanvas() {
        return mCanvas;
    }

    /**
     * Called during the scene's pre render pass. This is called in the main thread, so use this callback
     * to make any modifications you need to make to the layer tree.
     *
     * The default implementation performs an injection (see {@link Injector#inject(Object, Layer)})
     * @param ctx
     */
    protected void onPreRender(RenderContext ctx) {
        Injector.inject(this, this);
    }

    /**
     * Perform a render pass of this layer and all of its children
     * @param ctx
     */
    public final void render(RenderContext ctx) {
        ThreadEnforcer.BACKGROUND.enforce();
        if (getState() != RenderState.READY || mRenderContext == null) {
            // already finished
            return;
        }

        mState.setValue(RenderState.RENDERING);
        try {
            onRender(ctx);
        } catch (Throwable tr) {
            mState.setValue(RenderState.ERROR);
            ctx.handleException(this, tr);
        }

        for (Layer child : mChildNodes) {
            child.render(ctx);
        }
        publish();
    }

    protected void onRender(RenderContext ctx) {}

    public final void publish() {
        if (mRenderContext == null) {
            return;
        }

        if (mCanvas != null) {
            mCanvas.visibleProperty().bind(visibleProperty());
            mRenderContext.publish(this);
            mCanvas = null;
        }

        if (getState() != RenderState.ERROR) {
            mState.setValue(RenderState.PUBLISHED);
        }

        mRenderContext = null;
    }


    @Override
    public String toString() {
        return super.toString() + " [" + getName() + "]";
    }
}

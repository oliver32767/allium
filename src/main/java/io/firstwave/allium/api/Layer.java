package io.firstwave.allium.api;

import io.firstwave.allium.utils.ThreadEnforcer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;

/**
 * Created by obartley on 12/1/15.
 */
public class Layer implements Configurable {

    private final ObservableList<Layer> mChildNodes = FXCollections.observableArrayList();

    private Configuration mConfiguration;
    private Layer mParent;
    private RenderContext mRenderContext;
    private Canvas mCanvas;

    private final SimpleStringProperty mName = new SimpleStringProperty();
    private final SimpleBooleanProperty mVisible = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty mRendering = new SimpleBooleanProperty(false);

    ThreadEnforcer mThreadEnforcer = ThreadEnforcer.MAIN;

    public Layer() {
        this(null, null);
    }

    public Layer(String name) {
        this(name, null);
    }

    public Layer(String name, Configuration configuration) {
        setName(name);
        setConfiguration(configuration);
        mVisible.addListener((observable, oldValue, newValue) -> updateChildVisibility(newValue));
    }

    @Override
    public final Configuration getConfiguration() {
        return mConfiguration;
    }

    public final Canvas getCanvas() {
        return mCanvas;
    }

    /**
     * Main thread only
     */
    public final void setConfiguration(Configuration configuration) {
        mThreadEnforcer.enforce();
        if (configuration == null) {
            configuration = Configuration.EMPTY;
        }
        mConfiguration = configuration;
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

    public ObservableValue<String> nameProperty() {
        return mName;
    }

    public boolean isVisible() {
        return mVisible.getValue();
    }

    /**
     * Main thread only
     * @param visible
     */
    public void setVisible(boolean visible) {
        mThreadEnforcer.enforce();
        mVisible.setValue(visible);
    }

    private void updateChildVisibility(boolean visible) {
        for (Layer child : mChildNodes) {
            child.setVisible(visible);
        }
    }

    public BooleanProperty visibleProperty() {
        return mVisible;
    }

    public final ObservableValue<Boolean> renderingProperty() {
        return mRendering;
    }

    // CHILD NODE API /////////////////////////////////////////////////////////////////////////////////////////////////

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


    /**
     * Main thread only
     * @param child
     * @return
     */
    public Layer addChild(Layer child) {
        mThreadEnforcer.enforce();
        child.removeFromParent();
        child.mParent = this;
        mChildNodes.add(child);
        return child;
    }

    /**
     * Main thread only
     * @param child
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
    public final void removeAllChildren() {
        mThreadEnforcer.enforce();
        for (int i = mChildNodes.size() - 1; i >= 0; i--) {
            removeChildInternal(i);
        }
    }

    private void removeChildInternal(int index) {
        mChildNodes.get(index).mParent = null;
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

    final void onPreRender(RenderContext ctx) {
        mRendering.setValue(true);
        mRenderContext = ctx;
        mCanvas = onCreateCanvas(ctx);
    }

    /**
     * Executed in render thread
     */
    protected Canvas onCreateCanvas(RenderContext ctx) {
        return new Canvas(ctx.width, ctx.height);
    }

    /**
     * Executed in render thread
     */
    public final void publish() {
        if (isPublished()) {
            return;
        }
        mCanvas.visibleProperty().bind(visibleProperty());
        mRenderContext.publish(this);
        mRenderContext = null;
        mCanvas = null;
    }

    /**
     * Render thread
     */
    public final boolean isPublished() {
        return mRenderContext == null;
    }

    /**
     * Executed in render thread
     */
    public final void onPostRender() {
       publish();
    }

    // VISITOR API /////////////////////////////////////////////////////////////////////////////////////////////////////

    void each(Visitor visitor) {
        visitor.visit(this);
        for (Layer child : mChildNodes) {
            child.each(visitor);
        }
    }

    interface Visitor {
        void visit(Layer layer);
    }

    @Override
    public String toString() {
        return super.toString() + " [" + getName() + "]";
    }
}

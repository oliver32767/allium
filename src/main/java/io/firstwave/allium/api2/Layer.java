package io.firstwave.allium.api2;

import io.firstwave.allium.api.Configuration;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;

/**
 * Created by obartley on 12/1/15.
 */
public class Layer implements Configurable {

    private Configuration mConfiguration;
    private Canvas mCanvas;
    private LayerGroup mParent;

    private SimpleBooleanProperty mVisible = new SimpleBooleanProperty(true);
    private final ChangeListener<Boolean> mVisibiltyListener = (observable, oldValue, newValue) -> visibleProperty().set(newValue);

    public Layer() {
        this(null);
    }

    public Layer(Configuration configuration) {
        mConfiguration = configuration;
    }

    final void setParent(LayerGroup parent) {
        if (mParent != null) {
            mParent.visibleProperty().removeListener(mVisibiltyListener);
        }
        mParent = parent;
        if (mParent == null) {
            return;
        }
        mParent.visibleProperty().addListener(mVisibiltyListener);
    }


    public Canvas getCanvas() {
        return mCanvas;
    }

    void setCanvas(Canvas canvas) {
        mCanvas = canvas;
        mCanvas.visibleProperty().bind(visibleProperty());
    }

    @Override
    public Configuration getConfiguration() {
        return mConfiguration == null ? Configuration.EMPTY : mConfiguration;
    }

    public void setConfiguration(Configuration configuration) {
        mConfiguration = configuration;
    }

    public SimpleBooleanProperty visibleProperty() {
        return mVisible;
    }
}

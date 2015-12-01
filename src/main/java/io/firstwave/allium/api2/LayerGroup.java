package io.firstwave.allium.api2;

import io.firstwave.allium.api.Configuration;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.canvas.Canvas;

import java.util.LinkedHashMap;

/**
 * Created by obartley on 12/1/15.
 */
public abstract  class LayerGroup extends Layer {

    final ObservableMap<String, Layer> mLayers = FXCollections.observableMap(new LinkedHashMap<>());
    private final SimpleBooleanProperty mIsRendering = new SimpleBooleanProperty(false);

    public LayerGroup() {
        super();
    }

    public LayerGroup(Configuration configuration) {
        super(configuration);
    }

    protected final void putLayer(String key, Layer layer) {
        if (mIsRendering.get()) {
            throw new RenderException("Can't modify layer list during render pass!");
        }
        layer.setParent(this);
        mLayers.put(key, layer);
    }

    public final Layer getLayer(String key) {
        return mLayers.get(key);
    }

    public final void render(double width, double height) {
        mIsRendering.setValue(true);
        for (String key : mLayers.keySet()) {
            final Layer layer = mLayers.get(key);
            layer.setCanvas(new Canvas(width, height));
            // TODO: figure out how to render nested LayerGroups
            // for now, they're not even having their canvas reset
//            if (layer instanceof LayerGroup) {
//                ((LayerGroup) layer).render(width, height);
//            }
        }
        onRender();
        mIsRendering.setValue(false);
    }

    protected abstract void onRender();

    public ObservableValue<Boolean> renderingProperty() {
        return mIsRendering;
    }
}

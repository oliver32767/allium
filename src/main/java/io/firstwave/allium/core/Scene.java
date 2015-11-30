package io.firstwave.allium.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by obartley on 11/29/15.
 */
public abstract class Scene implements Configurable {
    private final List<Layer> mLayers = new ArrayList<Layer>();
    private final ObservableList<Layer> mObservableLayers = FXCollections.observableArrayList(mLayers);

    private Configuration mConfiguration = Configuration.EMPTY;

    public Scene() {}

    public final ObservableList<Layer> getLayerList() {
        return mObservableLayers;
    }

    protected final void setConfiguration(Configuration configuration) {
        mConfiguration =  configuration == null ? Configuration.EMPTY : configuration;
    }

    protected final LayerEditor addLayer(Layer layer) {
        mObservableLayers.remove(layer);
        mObservableLayers.add(layer);
        return new LayerEditor(layer);
    }

    @Override
    public final Configuration getConfiguration() {
        return mConfiguration;
    }

    public final void load() {
        onLoad();
    }

    protected abstract void onLoad();

    public static class LayerEditor {
        private final Layer mLayer;

        public LayerEditor(Layer layer) {
            mLayer = layer;
        }

        public LayerEditor setName(String name) {
            mLayer.setName(name);
            return this;
        }
        public LayerEditor setVisibile(boolean visibility) {
            mLayer.setVisible(visibility);
            return this;
        }
    }
}

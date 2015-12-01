package io.firstwave.allium.api;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by obartley on 11/29/15.
 */
public abstract class Scene implements Configurable {
    private final ObservableList<Layer> mLayers = FXCollections.observableArrayList(new ArrayList<Layer>());

    private Configuration mConfiguration = Configuration.EMPTY;

    private double mWidth = 1024;
    private double mHeight = 1024;
    private Color mBackgroundColor = Color.TRANSPARENT;

    public Scene() {}

    public final ObservableList<Layer> getLayerList() {
        return mLayers;
    }

    protected final void setConfiguration(Configuration configuration) {
        mConfiguration =  configuration == null ? Configuration.EMPTY : configuration;
    }

    protected final LayerEditor addLayer(Layer layer) {
        mLayers.remove(layer);
        mLayers.add(layer);
        return new LayerEditor(layer);
    }

    @Override
    public final Configuration getConfiguration() {
        return mConfiguration;
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
        return mBackgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public final void load() {
        onLoad();
    }

    protected abstract void onLoad();


    public final Renderer createRenderer() {
        final Renderer rv = onCreateRenderer();

        return rv != null ? rv : new Renderer() {
            final double w = getWidth();
            final double h = getHeight();
            @Override
            public Canvas render(Layer layer) {
                final Canvas canvas = new Canvas(w, h);
                layer.render(canvas);
                return canvas;
            }
        };
    }

    protected Renderer onCreateRenderer() {
        return null;
    }

    public static class LayerEditor {
        private final Layer mLayer;

        public LayerEditor(Layer layer) {
            mLayer = layer;
        }

        public LayerEditor setName(String name) {
            mLayer.setName(name);
            return this;
        }
        public LayerEditor setVisibility(Visibility visibility) {
            mLayer.setVisibility(visibility);
            return this;
        }
    }
}

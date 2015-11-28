package io.firstwave.allium.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by obartley on 11/27/15.
 */
public abstract class Scene {

    private Observer mObserver;

    public abstract int getLayerCount();
    public abstract Layer getLayerAt(int i);

    public String getLabelForLayer(int i) {
        return getLayerAt(i).getClass().getSimpleName();
    }

    public boolean getDefaultVisibilityForLayer(int i) {
        return true;
    }

    protected void notifySceneChanged() {
        if (mObserver != null) {
            mObserver.onSceneChanged(this);
        }
    }

    public void setObserver(Observer observer) {
        mObserver = observer;
    }

    public static List<Layer> collectLayers(Scene scene) {
        final int count = scene.getLayerCount();
        final List<Layer> layers = new ArrayList<Layer>(count);
        for (int i = 0; i < count; i++) {
            layers.add(scene.getLayerAt(i));
        }
        return layers;
    }

    public interface Observer {
        void onSceneChanged(Scene scene);
    }
}

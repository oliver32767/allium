package io.firstwave.allium.demo;

import io.firstwave.allium.core.Layer;
import io.firstwave.allium.core.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by obartley on 11/27/15.
 */
public class DemoScene extends Scene {
    private List<Layer> mLayers = new ArrayList<Layer>();

    public DemoScene() {
        mLayers.add(new NoiseLayer());
        mLayers.add(new GlyphLayer());
        mLayers.add(new GlyphLayer());
        mLayers.add(new GlyphLayer());
    }

    @Override
    public int getLayerCount() {
        return mLayers.size();
    }

    @Override
    public Layer getLayerAt(int i) {
        return mLayers.get(i);
    }
}

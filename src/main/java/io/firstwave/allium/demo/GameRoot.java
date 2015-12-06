package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.layer.NoiseLayer;

/**
 * Created by obartley on 12/5/15.
 */
public class GameRoot extends Layer {
    public GameRoot() {
        addChild(new NoiseLayer());
    }
}

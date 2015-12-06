package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.layer.GridLayer;
import io.firstwave.allium.api.layer.NoiseLayer;

/**
 * Created by obartley on 12/5/15.
 */
public class GameRoot extends Layer {
    public GameRoot() {
        Layer gl = new GridLayer();
        gl.setVisible(false);
        addChild(gl);
        addChild(new NoiseLayer());
    }
}

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
        NoiseLayer nl = new NoiseLayer();
        addChild(nl);

        Layer spam = new Layer("spam");

        for (int i = 0; i < 5; i++) {
            Layer l = new Layer();
            for (int t = 0; t < 5; t++) {
                TestLayer tl = new TestLayer();
                tl.addChild(new TestLayer());
                tl.addChild(new TestLayer());
                l.addChild(tl);
            }
            spam.addChild(l);
        }

        addChild(spam);

    }
}

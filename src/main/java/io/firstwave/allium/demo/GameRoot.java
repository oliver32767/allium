package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.layer.GridLayer;
import io.firstwave.allium.api.layer.NoiseLayer;

/**
 * Created by obartley on 12/5/15.
 */
public class GameRoot extends Layer {

    @Inject
    NoiseLayer noise;

    @Inject
    FillLayer fill;

    public GameRoot() {
        Layer gl = new GridLayer();
        gl.setVisible(false);
        addChild(gl);
        NoiseLayer nl = new NoiseLayer("noise");
        addChild(nl);
        addChild(new FillLayer("fill"));

        Layer spam = new Layer("spam");

        for (int i = 0; i < 5; i++) {
            spam.addChild(new TestLayer());
        }

        addChild(spam);

    }

    @Override
    protected void onRender(RenderContext ctx) {
        super.onRender(ctx);
        fill.render(ctx);
        noise.render(ctx);
    }
}

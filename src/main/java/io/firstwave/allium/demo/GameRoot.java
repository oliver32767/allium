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

    public GameRoot() {
        Layer gl = new GridLayer();
        gl.setVisible(false);
        addChild(gl);
        addChild(new NoiseLayer("noise"));
    }

    @Override
    protected void onRender(RenderContext ctx) {
        super.onRender(ctx);
        noise.render(ctx);
    }
}

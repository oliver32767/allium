package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.layer.GridLayer;
import io.firstwave.allium.api.layer.NoiseLayer;
import io.firstwave.allium.demo.riemann.RiemannLayer;
import javafx.scene.paint.Color;
import org.pmw.tinylog.Logger;

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
        NoiseLayer nl = new NoiseLayer("value", Color.BLUE, Color.ORANGE);
        nl.setSeedTransformer(param -> {
            Logger.warn("DERP DERP DEPR : " + param);
            return param + 1 * 2;
        });
        addChild(nl);
        addChild(new NoiseLayer("noise"));
        addChild(new RiemannLayer("riemann"));
    }

    @Override
    protected void onRender(RenderContext ctx) {
        super.onRender(ctx);
        noise.render(ctx);
    }
}

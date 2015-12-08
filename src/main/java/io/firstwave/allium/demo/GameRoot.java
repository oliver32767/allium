package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.layer.AnnotationLayer;
import io.firstwave.allium.api.layer.GridLayer;
import io.firstwave.allium.api.layer.NoiseLayer;
import io.firstwave.allium.demo.riemann.RiemannLayer;
import javafx.scene.paint.Color;

/**
 * Created by obartley on 12/5/15.
 */
public class GameRoot extends Layer {

    @Inject
    NoiseLayer noise;

    @Inject
    RiemannLayer riemann;

    @Inject
    AnnotationLayer anno;

    public GameRoot() {
        Layer gl = new GridLayer();
        gl.setVisible(false);
        addChild(gl);
        NoiseLayer nl = new NoiseLayer("value", Color.BLUE, Color.ORANGE);
        nl.setSeedTransformer(param -> (param + 1) * 2);
        addChild(nl);
        addChild(new NoiseLayer("noise"));
        addChild(new RiemannLayer("riemann"));
        addChild(new AnnotationLayer("anno"));
    }

    @Override
    protected void onRender(RenderContext ctx) {
        super.onRender(ctx);
        noise.render(ctx);
        riemann.render(ctx);

        for (int i = 0; i < riemann.getShapes().size() && i < 100; i++) {
            anno.addAnnotation(new AnnotationLayer.Annotation(String.valueOf(i), riemann.getShapes().get(i).x, riemann.getShapes().get(i).y));
        }
    }
}

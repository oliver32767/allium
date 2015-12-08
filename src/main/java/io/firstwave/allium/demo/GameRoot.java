package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.layer.AnnotationLayer;
import io.firstwave.allium.api.layer.GridLayer;
import io.firstwave.allium.api.layer.NoiseLayer;
import io.firstwave.allium.api.options.DoubleOption;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.demo.riemann.RiemannLayer;
import javafx.scene.canvas.GraphicsContext;
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

    @Inject
    Layer sequence;

    @Inject
    private double sequenceDecay;

    private static final int T_MINUS = 5;

    public GameRoot() {
        super(null, Options.create()
                .add("sequenceDecay", new DoubleOption(0.95, 0, 1))
                .build()
        );
        Layer gl = new GridLayer();
        gl.setVisible(false);
        addChild(gl);
        NoiseLayer nl = new NoiseLayer("value", Color.BLUE, Color.ORANGE);
        nl.setSeedTransformer(param -> (param + 1) * 2);
        addChild(nl);
        addChild(new NoiseLayer("noise"));
        addChild(new RiemannLayer("riemann"));
        addChild(new AnnotationLayer("anno"));
        addChild(new Layer("sequence"));
    }

    @Override
    protected void onRender(RenderContext ctx) {
        super.onRender(ctx);
        noise.render(ctx);
        riemann.render(ctx);

        double x = -1;
        double y = -1;

        GraphicsContext gc = sequence.getCanvas().getGraphicsContext2D();
        Color c = Color.PINK;

        double alpha;
        final double decay = sequenceDecay;

        for (int i = 0; i < riemann.getShapes().size() && i < 100; i++) {
            final double newX = riemann.getShapes().get(i).x;
            final double newY = riemann.getShapes().get(i).y;

            if (x >= 0) {
                gc.setStroke(c);
                gc.strokeLine(x, y, newX, newY);
            }

            alpha = c.getOpacity() * decay;
            if (alpha < 0) {
                break;
            }

            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);

            x = newX;
            y = newY;


            anno.addAnnotation(new AnnotationLayer.Annotation(String.valueOf(i - T_MINUS),
                    riemann.getShapes().get(i).x,
                    riemann.getShapes().get(i).y));
        }
    }
}

package io.firstwave.allium.demo.riemann;

import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.inject.Injector;
import io.firstwave.allium.api.options.ColorOption;
import io.firstwave.allium.api.options.DoubleOption;
import io.firstwave.allium.api.options.Options;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by obartley on 12/8/15.
 */
public class CircleShapeAdapter extends ShapeAdapter<CircleShapeAdapter.Circle> {

    @Inject
    private Color strokeColor;
    @Inject
    private Color fillColor;
    @Inject
    private double alphaDecay;
    @Inject
    private double minAlpha;



    public static Options getOptions() {
        return Options.create()
                .add("strokeColor", new ColorOption(Color.WHITE))
                .add("fillColor", new ColorOption(Color.TRANSPARENT))
                .add("alphaDecay", new DoubleOption(0, 0, 1))
                .add("minAlpha", new DoubleOption(0.125, 0, 1))
                .build();
    }

    @Override
    public boolean isColliding(RiemannLayer layer, Circle shape1, Circle shape2) {

        final double minDist = shape1.r + shape2.r;
        final double dist =
                Math.sqrt(
                        Math.pow((shape1.x - shape2.x), 2) +
                                Math.pow((shape1.y - shape2.y), 2));

        return dist <= minDist;
    }

    @Override
    public Circle onCreateShape(RiemannLayer layer, double x, double y, double area, int iteration) {
        return new Circle(x, y, area, iteration);
    }

    @Override
    public void onRender(RiemannLayer layer, Circle shape) {
        final GraphicsContext gc = layer.getCanvas().getGraphicsContext2D();

        if (shape.iteration == 1) {
            Injector.inject(this, layer);
        }

        double alpha = Math.pow((1 - alphaDecay), shape.iteration);

        if (alpha < minAlpha) {
            alpha = minAlpha;
        }

        gc.setStroke(new Color(
                strokeColor.getRed(),
                strokeColor.getGreen(),
                strokeColor.getBlue(),
                alpha));

        gc.setFill(new Color(
                fillColor.getRed(),
                fillColor.getGreen(),
                fillColor.getBlue(),
                alpha
        ));

        gc.fillOval(shape.x - shape.r, shape.y - shape.r, shape.r * 2, shape.r * 2);
        gc.strokeOval(shape.x - shape.r, shape.y - shape.r, shape.r * 2, shape.r * 2);
    }

    public static class Circle extends ShapeAdapter.Shape {
        public final double r;

        public Circle(double x, double y, double area, int iteration) {
            super(x, y, area, iteration);
            r = Math.sqrt(area / Math.PI);
        }
    }
}

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
public class SquareShapeAdapter extends ShapeAdapter<SquareShapeAdapter.Square> {

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
    public boolean isColliding(RiemannLayer layer, Square shape1, Square shape2) {


        return isColliding(shape1, shape2.left, shape2.top) ||
                isColliding(shape1, shape2.right, shape2.top) ||
                isColliding(shape1, shape2.right, shape2.bottom) ||
                isColliding(shape1, shape2.left, shape2.bottom);
    }

    private boolean isColliding(Square square, double x, double y) {
        return (x > square.left && x < square.right && y > square.top && y < square.bottom);
    }

    @Override
    public Square onCreateShape(RiemannLayer layer, double x, double y, double area, int iteration) {
        return new Square(x, y, area, iteration);
    }

    @Override
    public void onRender(RiemannLayer layer, Square shape) {
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
                strokeColor.getOpacity() * alpha));

        gc.setFill(new Color(
                fillColor.getRed(),
                fillColor.getGreen(),
                fillColor.getBlue(),
                fillColor.getOpacity() * alpha
        ));

        gc.fillRect(shape.left, shape.top, shape.right - shape.left, shape.bottom - shape.top);
        gc.strokeRect(shape.left, shape.top, shape.right - shape.left, shape.bottom - shape.top);
    }

    public static class Square extends ShapeAdapter.Shape {
        public final double left, top, right, bottom;

        public Square(double x, double y, double area, int iteration) {
            super(x, y, area, iteration);

            final double edge = Math.sqrt(area);
            left = x - edge / 2;
            top = y - edge / 2;
            right = x + edge / 2;
            bottom = y + edge / 2;
        }
    }
}

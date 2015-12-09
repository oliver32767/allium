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
    public boolean isOverlapping(RiemannLayer layer, Square shape1, Square shape2) {
        return shape1.isOverlapping(shape2);
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

        public boolean isOverlapping(Square square) {
            return contains(square.left, square.top) ||
                    contains(square.right, square.top) ||
                    contains(square.right, square.bottom) ||
                    contains(square.left, square.bottom);
        }

        public boolean contains(double x1, double y1) {
            return (x1 > left && x1 < right && y1 > top && y1 < bottom);
        }
    }
}

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
public class MultiShapeAdapter extends ShapeAdapter<ShapeAdapter.Shape> {

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
    public boolean isOverlapping(RiemannLayer layer, Shape shape1, Shape shape2) {
        if (shape1 instanceof CircleShapeAdapter.Circle) {
            if (shape2 instanceof CircleShapeAdapter.Circle) {
                return isColliding((CircleShapeAdapter.Circle) shape1, (CircleShapeAdapter.Circle) shape2);
            } else {
                return isColliding((CircleShapeAdapter.Circle) shape1, (SquareShapeAdapter.Square) shape2);
            }
        } else {
            if (shape2 instanceof CircleShapeAdapter.Circle) {
                return isColliding((CircleShapeAdapter.Circle) shape2, (SquareShapeAdapter.Square) shape1);
            } else {
                return isColliding((SquareShapeAdapter.Square) shape1, (SquareShapeAdapter.Square) shape2);
            }
        }
    }

    private boolean isColliding(CircleShapeAdapter.Circle circle1, CircleShapeAdapter.Circle circle2) {
        return circle1.isOverlapping(circle2);
    }

    private boolean isColliding(CircleShapeAdapter.Circle circle, SquareShapeAdapter.Square square) {
        if (square.contains(circle.x, circle.y)) {
            return true;
        }

        return circle.contains(square.left, square.top) ||
                circle.contains(square.right, square.top) ||
                circle.contains(square.left, square.bottom) ||
                circle.contains(square.right, square.bottom);
    }

    private boolean isColliding(SquareShapeAdapter.Square square1, SquareShapeAdapter.Square square2) {
        return square1.isOverlapping(square2);
    }

    @Override
    public Shape onCreateShape(RiemannLayer layer, double x, double y, double area, int iteration) {
        if ((iteration & 1) == 0) {
            return new CircleShapeAdapter.Circle(x, y, area, iteration);
        } else {
            return new SquareShapeAdapter.Square(x, y, area, iteration);
        }
    }

    @Override
    public void onRender(RiemannLayer layer, Shape shape) {
        if (shape instanceof CircleShapeAdapter.Circle) {
            onRender(layer, (CircleShapeAdapter.Circle) shape);
        } else {
            onRender(layer, (SquareShapeAdapter.Square) shape);
        }
    }

    private void onRender(RiemannLayer layer, CircleShapeAdapter.Circle shape) {
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

    private void onRender(RiemannLayer layer, SquareShapeAdapter.Square shape) {
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

}

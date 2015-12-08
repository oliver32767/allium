package io.firstwave.allium.demo.riemann;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.ColorOption;
import io.firstwave.allium.api.options.DoubleOption;
import io.firstwave.allium.api.options.IntegerOption;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.gen.math.Riemann;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by obartley on 12/7/15.
 */
public class RiemannLayer extends Layer {

    private ShapeAdapter mShapeAdapter;

    @Inject
    private double c;
    @Inject
    private int iterations;
    @Inject
    private int tolerance;
    @Inject
    private double minorThreshold;
    @Inject
    private Color strokeColor;
    @Inject
    private double minorOpacity;

    public RiemannLayer() {
        this(null);
    }

    public RiemannLayer(String name) {
        super(name, Options.create()
                .add("c", new DoubleOption(1.05, 1.0, 1.5))
                .add("iterations", new IntegerOption(1000, 1, 100000))
                .add("tolerance",
                        "If a shape cannot be placed randomly, placement will be retried this many times before halting iteration",
                        new IntegerOption(100, 0, 10000))

                .addSeparator("Visualization")
                .add("strokeColor", new ColorOption(Color.WHITE))
                .add("minorThreshold", new DoubleOption(0.1, 0, 1))
                .add("minorOpacity", "Opacity for shapes with areas where a,n/a,0 < minorThreshold",
                        new DoubleOption(0.25, 0, 1))
                .build()
        );

        setShapeAdapter(new ShapeAdapter<Circle>() {
            @Override
            public boolean isColliding(Circle shape, double x, double y, double area, int iteration) {
                final double r = Math.sqrt(area / Math.PI);
                final double r1 = Math.sqrt(shape.area / Math.PI);

                final double minDist2 = r + r1;
                final double dist2 =
                        Math.sqrt(
                                Math.pow((x - shape.x), 2) +
                                        Math.pow((y - shape.y), 2));

                return dist2 <= minDist2;
            }

            @Override
            public Circle onCreateShape(double x, double y, double area, int iteration) {
                return new Circle(x, y, area, iteration);
            }

            @Override
            public void onRender(Circle shape, Canvas canvas) {
                final double r = Math.sqrt(shape.area / Math.PI);

                if (r > minorThreshold) {
                    canvas.getGraphicsContext2D().setStroke(strokeColor);
                } else {
                    final Color minorColor = new Color(strokeColor.getRed(),
                            strokeColor.getGreen(),
                            strokeColor.getBlue(),
                            minorOpacity);
                    canvas.getGraphicsContext2D().setStroke(minorColor);
                }

                canvas.getGraphicsContext2D()
                        .strokeOval(
                                shape.x - r, shape.y - r, r * 2, r * 2);
            }


        });
    }

    public ShapeAdapter getShapeAdapter() {
        return mShapeAdapter;
    }

    public void setShapeAdapter(ShapeAdapter shapeAdapter) {
        mShapeAdapter = shapeAdapter;
    }

    @Override
    protected void onRender(RenderContext ctx) {
        final double w = getScene().getWidth();
        final double h = getScene().getHeight();
        final double totalArea = w * h;

        // calculate the riemann-zeta sum using the value selected for c
        final double rzSum = Riemann.zeta(new double[]{c, 0})[0]; // we only want the real part

        // now calculate the ratio between our riemann sum and the total area of the scene
        final double rzRatio = (totalArea) / rzSum;

        boolean placed;

        int iter = 1;
        long tests = 0;

        final List<ShapeAdapter.Shape> mShapes = new ArrayList<>();

        double accumulatedArea = 0;

        while (true) {
            final double area = g(iter, c) * rzRatio;

            // A/π = r2
            final double r = Math.sqrt(area / Math.PI);

            placed = false;
            for (int i = 0; i < tolerance; i++) {
                final double x = rand(ctx.getRandom(), r, w - r);
                final double y = rand(ctx.getRandom(), r, h - r);

                placed = true;
                for (ShapeAdapter.Shape shape : mShapes) {
                    tests++;
                    if (mShapeAdapter.isColliding(shape, x, y, area, iter)) {
                        placed = false;
                        break;
                    }
                }

                // we've placed a circle, exit the tolerance loop
                if (placed) {
                    mShapes.add(mShapeAdapter.onCreateShape(x, y, area, iter));
                    accumulatedArea += area;
                    break;
                }
            }

            if (!placed) {
                break;
            } else {
                iter++;
                if (iter > iterations) {
                    break;
                }
            }
        }

        final double efficiency = (accumulatedArea / totalArea) * 100;
        if (iter > iterations) {
            iter--;
            setMessage(String.format("complete (%.4f%%)", efficiency));
        } else {
            setMessage(String.format("failed @ %d (%.4f%%)", iter, efficiency));
        }


        ctx.handleMessage(this, String.format("finished %d iterations with %d tests (%.4f%% efficient)", iter, tests, efficiency));

        final GraphicsContext gc = getCanvas().getGraphicsContext2D();

        gc.setLineWidth(2);

        final double r0 = Math.sqrt(g(1, c) * rzRatio / Math.PI);



        for (ShapeAdapter.Shape shape : mShapes) {
            mShapeAdapter.onRender(shape, getCanvas());

        }
    }

    private static double g(double i, double c) {
        return 1 / Math.pow(i, c);
    }

    private static double rand(Random rnd, double min, double max) {
        return min + (max - min) * rnd.nextDouble();
    }


    public static class Circle extends ShapeAdapter.Shape {

        private Circle(double x, double y, double area, int iteration) {
            super(x, y, area, iteration);
        }
    }
}

package io.firstwave.allium.demo.riemann;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.DoubleOption;
import io.firstwave.allium.api.options.IntegerOption;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.gen.math.Riemann;
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
    private final List<ShapeAdapter.Shape> mShapes = new ArrayList<>();

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
        this(name, null);
    }

    public RiemannLayer(String name, Options options) {
        super(name, Options.buildUpon(options)
                .add("c", new DoubleOption(1.05, 1.0, 1.5))
                .add("iterations", new IntegerOption(1000, 1, 100000))
                .add("tolerance",
                        "If a shape cannot be placed randomly, placement will be retried this many times before halting iteration",
                        new IntegerOption(100, 0, 10000))

                .addSeparator("Visualization")

                .add("minorThreshold", new DoubleOption(0.1, 0, 1))
                .add("minorOpacity", "Opacity for shapes with areas where a,n/a,0 < minorThreshold",
                        new DoubleOption(0.25, 0, 1))
                .build()
        );
    }

    public ShapeAdapter getShapeAdapter() {
        return mShapeAdapter;
    }

    public void setShapeAdapter(ShapeAdapter shapeAdapter) {
        mShapeAdapter = shapeAdapter;
    }

    public List<ShapeAdapter.Shape> getShapes() {
        return mShapes;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRender(RenderContext ctx) {

        final ShapeAdapter adapter = mShapeAdapter;

        if (adapter == null) {
            ctx.handleMessage(this, "No ShapeAdapter -- skipping render");
            return;
        }

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

        mShapes.clear();

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
                    if (mShapeAdapter.isColliding(this, shape, x, y, area, iter)) {
                        placed = false;
                        break;
                    }
                }

                // we've placed a circle, exit the tolerance loop
                if (placed) {
                    mShapes.add(mShapeAdapter.onCreateShape(this, x, y, area, iter));
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

        for (ShapeAdapter.Shape shape : mShapes) {
            mShapeAdapter.onRender(this, shape);
        }
    }

    private static double g(double i, double c) {
        return 1 / Math.pow(i, c);
    }

    private static double rand(Random rnd, double min, double max) {
        return min + (max - min) * rnd.nextDouble();
    }
}

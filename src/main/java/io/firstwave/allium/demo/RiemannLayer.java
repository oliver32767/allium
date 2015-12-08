package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.ColorOption;
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

    @Inject
    private double c;
    @Inject
    private int iterations;
    @Inject
    private int tolerance;
    @Inject
    private double minArea;
    @Inject
    private Color majorColor;
    @Inject
    private Color minorColor;

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
                .add("minArea", new DoubleOption(0, 0, 1))
                .addSeparator()
                .add("majorColor", new ColorOption(Color.WHITE))
                .add("minorColor", new ColorOption(new Color(1, 1, 1, 0.125)))
                .build()
        );
    }

    @Override
    protected void onRender(RenderContext ctx) {
        final double w = getScene().getWidth();
        final double h = getScene().getHeight();

        ctx.handleMessage(this, "total area:" + w * h);

        // calculate the riemann-zeta sum using the value selected for c
        final double rzSum = Riemann.zeta(new double[] {c, 0})[0]; // we only want the real part

        ctx.handleMessage(this, "riemann area:" + rzSum + " (c = " + c + ")");

        // now calculate the ratio between our riemann sum and the total area of the scene
        final double rzRatio = (w * h) / rzSum;
        ctx.handleMessage(this, "rz ratio: " + rzRatio);

        boolean halt = false;
        boolean placed = false;

        int iter = 1;
        long tests = 0;

        final List<Circle> mCircles = new ArrayList<>();

        while (!halt) {
            final double area = g(iter, c) * rzRatio;

            // A/π = r2
            final double r = Math.sqrt(area / Math.PI);

            placed = false;
            for (int i = 0; i < tolerance; i++) {
                final double x = rand(ctx.getRandom(), r, w - r);
                final double y = rand(ctx.getRandom(), r, h - r);

                placed = true;
                for (Circle circle : mCircles) {
                    tests++;
                    if (circle.isCollision(x, y, r)) {
                        placed = false;
                        break;
                    }
                }

                // we've placed a circle, exit the tolerance loop
                if (placed) {
                    mCircles.add(new Circle(x, y, r));
                    break;
                }
            }

            if (!placed) {
                halt = true;
            } else {
                iter++;
                if (iter > iterations) {
                    halt = true;
                }
            }
        }

        if (iter > iterations) {
            setMessage("complete");
        } else {
            setMessage("failed @ " + iter);
        }

        final GraphicsContext gc = getCanvas().getGraphicsContext2D();

        gc.setLineWidth(2);

        final double r0 = Math.sqrt(g(1, c) * rzRatio / Math.PI);

        for (Circle circle : mCircles) {

            final double r = circle.r / r0;

            if (r > minArea) {
                gc.setStroke(majorColor);
            } else {
                gc.setStroke(minorColor);
            }

            gc.strokeOval(circle.x - circle.r, circle.y - circle.r, circle.r * 2, circle.r * 2);
        }
    }

    private static double g(double i, double c) {
        return 1 / Math.pow(i, c);
    }

    private static double rand(Random rnd, double min, double max) {
        return min + (max - min) * rnd.nextDouble();
    }


    public static class Circle {
        public final double x;
        public final double y;
        public final double r;

        public Circle(double x, double y, double r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }

        public boolean isCollision(double x1, double y1, double r1) {

            final double minDist2 = r + r1;
            final double dist2 =
                    Math.sqrt(
                        Math.pow((x - x1), 2) +
                                Math.pow((y - y1), 2));

            return dist2 <= minDist2;
        }
    }
}

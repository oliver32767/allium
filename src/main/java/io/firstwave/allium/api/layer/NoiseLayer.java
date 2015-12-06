package io.firstwave.allium.api.layer;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.BooleanOption;
import io.firstwave.allium.api.options.FloatOption;
import io.firstwave.allium.api.options.IntegerOption;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.gen.Curve;
import io.firstwave.allium.gen.Interpolator;
import io.firstwave.allium.gen.RadialGradient;
import io.firstwave.allium.gen.noise.NoiseGenerator;
import io.firstwave.allium.gen.noise.SimplexNoiseGenerator;
import javafx.scene.paint.Color;


/**
 * Created by obartley on 12/2/15.
 */
public class NoiseLayer extends Layer {

    private NoiseGenerator mNoiseGenerator;

    private double[][] noise;
    private double[][] interpolation;

    @Inject private int octaves;
    @Inject private float frequency;
    @Inject private float amplitude;
    @Inject boolean normalized;
    @Inject float noiseScale;


    public NoiseLayer() {
        this(null);
    }

    public NoiseLayer(String name) {
        super(name);
        setOptions(Options.create()
                .add("octaves", new IntegerOption(1))
                .add("frequency", new FloatOption(1))
                .add("amplitude", new FloatOption(1))
                .add("noiseScale", new FloatOption(1, 0, 100))
                .add("normalized", new BooleanOption(false))
                .build()
        );
    }


    @Override
    protected void onPreRender(RenderContext ctx) {
        super.onPreRender(ctx);
        mNoiseGenerator = new SimplexNoiseGenerator(getScene().getRandom());
        noise = new double[(int)getScene().getWidth()][(int)getScene().getHeight()];
        final long startTime = System.currentTimeMillis();
        for (int x = 0; x < noise.length; x++) {
            for (int y = 0; y < noise[x].length; y++) {
                noise[x][y] = mNoiseGenerator.noise(x, y,
                        octaves,
                        frequency,
                        amplitude,
                        normalized);
            }
        }
        final float elapsed = (float) (System.currentTimeMillis() - startTime) / 1000;
        ctx.handleMessage(this, String.format("generated noise in %.4f second(s)\n", elapsed));
    }

    @Override
    protected void onRender(RenderContext ctx) {
        for (int x = 0; x < noise.length; x++) {
            for (int y = 0; y < noise[x].length; y++) {

                double a = (noise[x][y] + 1) / 2;

                if (a < 0) a = 0;
                if (a > 1) a = 1;

                final Color c = new Color(0, 1.0, 0, a);

                getCanvas().getGraphicsContext2D().getPixelWriter().setColor(x, y, c);
            }
        }
    }

    private double[][] getNoise(int size, NoiseGenerator generator) {
        double[][] rv = new double[size][size];
        for (int x = 0; x < rv.length; x++) {
            for (int y = 0; y < rv[0].length; y++) {
                rv[x][y] = generator.noise(x, y);
            }
        }
        return rv;
    }

    private double[][] getInterpolation(double[][] matrix, int size, Interpolator interpolator, Curve gradientCurve, float intensity, float thresholdMin, float thresholdMax) {
        RadialGradient gradient = new RadialGradient(gradientCurve);
        float scale = (float) matrix.length / (float) size;
        double value;
        double gx, gy;
        double[][] rv = new double[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                value = interpolator.get(matrix, (double) x * scale, (double) y * scale);
                gx = x - (size / 2);
                gy = y - (size / 2);
                gx = gx * (2.0f / size);
                gy = gy * (2.0f / size);
                value = normalize(value) * constrain(gradient.get(gx, gy) + intensity);
                if (value > thresholdMin && value < thresholdMax) {
                    rv[x][y] = value;
                } else {
                    rv[x][y] = 0.0;
                }
            }
        }
        return rv;
    }
    protected double normalize(double v) {
        return (v + 1) / 2f;
    }
    protected float constrain(float f) {
        if (f < 0.0f) return 0.0f;
        if (f > 1.0f) return 1.0f;
        return f;
    }
    protected double constrain(double d) {
        if (d < 0.0f) return 0.0f;
        if (d > 1.0f) return 1.0f;
        return d;
    }

}

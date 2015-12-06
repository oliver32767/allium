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
import javafx.scene.image.PixelWriter;
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

    @Inject private float noiseScale;

    @Inject private boolean normalized;

    @Inject private float intensity;
    @Inject private float thresholdMin;
    @Inject private float thresholdMax;


    public NoiseLayer() {
        this(null);
    }

    public NoiseLayer(String name) {
        super(name);
        setOptions(Options.create()
                .add("octaves", new IntegerOption(1))
                .add("frequency", new FloatOption(1))
                .add("amplitude", new FloatOption(1))
                .add("noiseScale", new FloatOption(0.1f, 0.01f, 1f))
                .addSeparator()
                .add("normalized", new BooleanOption(false))
                .add("intensity", new FloatOption(0.0f))
                .add("thresholdMin", new FloatOption(0.0f, 0.0f, 1.0f))
                .add("thresholdMax", new FloatOption(1.0f, 0.0f, 1.0f))
                .build()
        );
    }

    @Override
    protected void onPreRender(RenderContext ctx) {
        super.onPreRender(ctx);
        mNoiseGenerator = new SimplexNoiseGenerator(getScene().getRandom());

        float scaleX = (float) getScene().getWidth() * noiseScale;
        float scaleY = (float) getScene().getHeight() * noiseScale;

        noise = getNoise((int) (getScene().getWidth() / scaleX), (int) (getScene().getHeight() / scaleY), mNoiseGenerator);
        interpolation = getInterpolation(noise, (int) getScene().getWidth(), (int) getScene().getHeight(),
                Interpolator.CUBIC, Curve.CUBIC_OUT, intensity, thresholdMin, thresholdMax);
    }

    public double getValue(int x, int y) {
        try {
            return interpolation[x][y];
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return 0;
        }
    }

    @Override
    protected void onRender(RenderContext ctx) {

        final PixelWriter pw = getCanvas().getGraphicsContext2D().getPixelWriter();

        for (int y = 0; y < (int) getScene().getHeight(); y++) {
            for (int x = 0; x < (int) getScene().getWidth(); x++) {
                final Color c = new Color(0, 1, 0, getValue(x, y));
                pw.setColor(x, y, c);
            }
        }
    }

    private double[][] getNoise(int sizeX, int sizeY, NoiseGenerator generator) {
        double[][] rv = new double[sizeX][sizeY];
        for (int x = 0; x < rv.length; x++) {
            for (int y = 0; y < rv[0].length; y++) {
                rv[x][y] = generator.noise(x, y, octaves, frequency, amplitude, normalized);
            }
        }
        return rv;
    }

    private double[][] getInterpolation(double[][] matrix, int sizeX, int sizeY, Interpolator interpolator, Curve gradientCurve, float intensity, float thresholdMin, float thresholdMax) {
        RadialGradient gradient = new RadialGradient(gradientCurve);
        float scaleX = (float) matrix.length / (float) sizeX;
        float scaleY = (float) matrix[0].length / (float) sizeY;

        double value;
        double gx, gy;
        double[][] rv = new double[sizeX][sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeX; y++) {
                value = interpolator.get(matrix, (double) x * scaleX, (double) y * scaleY);
                gx = x - (sizeX / 2);
                gy = y - (sizeY / 2);
                gx = gx * (2.0f / sizeX);
                gy = gy * (2.0f / sizeY);
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

    protected double constrain(double d) {
        if (d < 0.0) return 0.0;
        if (d > 1.0) return 1.0;
        return d;
    }

}

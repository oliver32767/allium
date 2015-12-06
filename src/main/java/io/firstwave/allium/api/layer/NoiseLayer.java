package io.firstwave.allium.api.layer;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.*;
import io.firstwave.allium.gen.Curve;
import io.firstwave.allium.gen.Interpolator;
import io.firstwave.allium.gen.RadialGradient;
import io.firstwave.allium.gen.noise.NoiseGenerator;
import io.firstwave.allium.gen.noise.PerlinNoiseGenerator;
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

    @Inject private String generator;
    @Inject private int octaves;
    @Inject private float frequency;
    @Inject private float amplitude;

    @Inject private float noiseScale;

    @Inject private boolean normalized;

    @Inject private float intensity;
    @Inject private float thresholdMin;
    @Inject private float thresholdMax;
    @Inject private String interpolator;

    @Inject private Color positiveColor;
    @Inject private Color negativeColor;
    @Inject private boolean signed;

    @Inject private boolean flat;


    public NoiseLayer() {
        this(null);
    }

    public NoiseLayer(String name) {
        super(name);
        setOptions(Options.create()
                .addSeparator("Noise")
                .add("generator", new SingleChoiceOption("simplex", "simplex", "perlin"))
                .add("octaves", new IntegerOption(1, 1, 32))
                .add("frequency", new FloatOption(1))
                .add("amplitude", new FloatOption(1))
                .add("normalized", new BooleanOption(false))

                .addSeparator("Interpolation")
                .add("interpolator", new SingleChoiceOption(Curve.lookupOptions[0], Curve.lookupOptions))
                .add("intensity", new FloatOption(1f, 0f, 1f))
                .add("noiseScale", new FloatOption(0.1f, 0.01f, 1f))

                .addSeparator("Visualisation")
                .add("thresholdMin", new FloatOption(-1.0f))
                .add("thresholdMax", new FloatOption(1.0f))
                .add("flat", new BooleanOption(false))
                .add("positiveColor", new ColorOption(Color.GREEN))
                .add("negativeColor", new ColorOption(Color.RED))
                .add("signed", new BooleanOption(true))
                .build()
        );
    }

    @Override
    protected void onPreRender(RenderContext ctx) {
        super.onPreRender(ctx);
        if ("simplex".equals(generator)) {
            mNoiseGenerator = new SimplexNoiseGenerator(getScene().getRandom());
        } else {
            mNoiseGenerator = new PerlinNoiseGenerator(getScene().getRandom());
        }

        float scaleX = (float) getScene().getWidth() * noiseScale;
        float scaleY = (float) getScene().getHeight() * noiseScale;

        noise = getNoise((int) (getScene().getWidth() / scaleX), (int) (getScene().getHeight() / scaleY), mNoiseGenerator);
        interpolation = getInterpolation(noise, (int) getScene().getWidth(), (int) getScene().getHeight(),
                Interpolator.CUBIC, Curve.lookup(interpolator), intensity);
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
                Color c = positiveColor;
                double d = getValue(x, y);

                // modulo helps us visualize octaves
                if (d > 1) {
                    d = d % 1;
                } else if (d < -1){
                    d = d % -1;
                }

                if (d >= thresholdMin && d <= thresholdMax) {

                    if (signed) {
                        if (d < 0) {
                            c = negativeColor;
                            d = Math.abs(d);
                        }
                    } else {
                        // normalize
                        d += 1;
                        d = d * 0.5;
                    }

                    if (!flat) {
                        c = new Color(
                                c.getRed(),
                                c.getGreen(),
                                c.getBlue(),
                                c.getOpacity() * d);
                    }

                    pw.setColor(x, y, c);
                }

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

    private double[][] getInterpolation(double[][] matrix, int sizeX, int sizeY, Interpolator interpolator, Curve gradientCurve, float intensity) {
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
                value = value * (gradient.get(gx, gy) * intensity);

                rv[x][y] = value;
            }
        }
        return rv;
    }

}

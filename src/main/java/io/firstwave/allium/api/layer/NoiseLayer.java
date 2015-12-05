package io.firstwave.allium.api.layer;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.inject.Injector;
import io.firstwave.allium.api.options.BooleanOption;
import io.firstwave.allium.api.options.FloatOption;
import io.firstwave.allium.api.options.IntegerOption;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.gen.NoiseGenerator;
import io.firstwave.allium.gen.SimplexNoiseGenerator;
import javafx.scene.paint.Color;


/**
 * Created by obartley on 12/2/15.
 */
public class NoiseLayer extends Layer {

    private NoiseGenerator mNoiseGenerator;
    private double[][] noise;

    @Inject private int seed;
    @Inject private int octaves;
    @Inject private float frequency;
    @Inject private float amplitude;
    @Inject boolean normalized;



    public NoiseLayer() {
        this(null);
    }

    public NoiseLayer(String name) {
        super(name);
        setOptions(Options.create()
                .add("seed", new IntegerOption(0, Integer.MIN_VALUE, Integer.MAX_VALUE))
                .add("octaves", new IntegerOption(1))
                .add("frequency", new FloatOption(0))
                .add("amplitude", new FloatOption(0))
                .add("normalized", new BooleanOption(false))
                .build()
        );
    }

    public double[][] getNoise() {
        return noise;
    }

    @Override
    protected void onPreRender(RenderContext ctx) {
        Injector.inject(this, this);
        mNoiseGenerator = new SimplexNoiseGenerator(seed);
        noise = new double[(int)ctx.width][(int)ctx.height];
    }

    @Override
    protected void onRender(RenderContext ctx) {
        for (int x = 0; x < noise.length; x++) {
            for (int y = 0; y < noise[x].length; y++) {
                noise[x][y] = mNoiseGenerator.noise(x, y,
                        octaves,
                        frequency,
                        amplitude,
                        normalized);

                double a = (noise[x][y] + 1) / 2;

                if (a < 0) a = 0;
                if (a > 1) a = 1;

                final Color c = new Color(0, 1.0, 0, a);

                getCanvas().getGraphicsContext2D().getPixelWriter().setColor(x, y, c);
            }
        }
    }
}
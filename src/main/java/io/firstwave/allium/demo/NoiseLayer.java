package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.gen.NoiseGenerator;


/**
 * Created by obartley on 12/2/15.
 */
public class NoiseLayer extends Layer {

    private NoiseGenerator mNoiseGenerator;
    private double[][] noise;

//    public NoiseLayer() {
//        setOptions(new Configuration.Builder()
//                .addIntegerItem("seed", 0)
//                .addIntegerItem("octaves", 0)
//                .addFloatItem("frequency", 0)
//                .addFloatItem("amplitude", 0)
//                .addOptionItem("normalized", false)
//                .build());
//    }
//
//    public double[][] getNoise() {
//        return noise;
//    }
//
//    @Override
//    protected void onPreRender(RenderContext ctx) {
//        mNoiseGenerator = new SimplexNoiseGenerator(getOptions().getInteger("seed"));
//        noise = new double[(int)ctx.width][(int)ctx.height];
//    }
//
//    @Override
//    protected void onRender(RenderContext ctx) {
//        for (int x = 0; x < noise.length; x++) {
//            for (int y = 0; y < noise[x].length; y++) {
//                noise[x][y] = mNoiseGenerator.noise(x, y,
//                        getOptions().getInteger("octaves"),
//                        getOptions().getFloat("frequency"),
//                        getOptions().getFloat("amplitude"),
//                        getOptions().getOption("normalized"));
//
//                double a = (noise[x][y] + 1) / 2;
//
//                if (a < 0) a = 0;
//                if (a > 1) a = 1;
//
//                final Color c = new Color(0, 1.0, 0, a);
//
//                getCanvas().getGraphicsContext2D().getPixelWriter().setColor(x, y, c);
//            }
//        }
//    }
}

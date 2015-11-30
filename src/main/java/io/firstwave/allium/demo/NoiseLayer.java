package io.firstwave.allium.demo;

import io.firstwave.allium.core.Configuration;
import io.firstwave.allium.core.Layer;
import io.firstwave.allium.core.Scene;
import io.firstwave.allium.gen.SimplexNoiseGenerator;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;

import java.awt.*;
import java.util.Random;

/**
 * Created by obartley on 11/27/15.
 */
public class NoiseLayer extends Layer {

    public NoiseLayer(Scene scene, Configuration configuration) {
        super(scene, configuration);
    }

    @Override
    public Canvas render() {
        SimplexNoiseGenerator gen = new SimplexNoiseGenerator(new Random());

        Canvas rv = new Canvas(1024, 1024);
        final GraphicsContext gc = rv.getGraphicsContext2D();
        final PixelWriter pw = gc.getPixelWriter();


        final int r = getConfiguration().getInteger("r");
        final int g = getConfiguration().getInteger("g");
        final int b = getConfiguration().getInteger("b");

        for (int y = 0; y < 1024; y++) {
            for (int x = 0; x < 1024; x++) {
                final double noise = gen.noise(x, y);
                final int a;
                if (noise < 0) {
                    a = 0;
                } else {
                    a = 128;
                }
                pw.setArgb(x, y, new Color(r,g,b,a).getRGB());
            }
        }
        return rv;
    }
}

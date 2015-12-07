package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.pmw.tinylog.Logger;

import java.util.Random;

/**
 * Created by obartley on 12/6/15.
 */
public class TestLayer extends Layer {

    @Override
    protected void onPreRender(RenderContext ctx) {
        super.onPreRender(ctx);
        final int r = ctx.getRandom().nextInt(10);
        if (r > 8) {
            Logger.warn("Adding new test layer");
            addChild(new TestLayer());
        } else if (r > 6) {
            if (getChildCount() > 0) {

            }
        }
    }

    @Override
    protected void onRender(RenderContext ctx) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Random r = ctx.getRandom();

        final Color c = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), 0.5 * r.nextDouble() + 0.5);

        final GraphicsContext gc = getCanvas().getGraphicsContext2D();

        gc.setFill(c);
        gc.fillRect(getScene().getWidth() * r.nextDouble(),
                getScene().getHeight() * r.nextDouble(),
                getScene().getWidth() * r.nextDouble(),
                getScene().getHeight() * r.nextDouble());
        setMessage("r:" + ctx.getRandom().nextInt(32767));
    }
}

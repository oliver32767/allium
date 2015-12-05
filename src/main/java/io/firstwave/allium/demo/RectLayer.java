package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import javafx.scene.paint.Color;

import java.util.Random;

import static io.firstwave.allium.demo.LayerUtils.gc;


/**
 * Created by obartley on 12/4/15.
 */
public class RectLayer extends Layer {

    @Override
    protected void onRender(RenderContext ctx) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Random r = ctx.getRandom();

        final Color c = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), 0.5 * r.nextDouble() + 0.5);

        gc(this).setFill(c);
        gc(this).fillRect(ctx.width * r.nextDouble(), ctx.height * r.nextDouble(), ctx.width * r.nextDouble(), ctx.height * r.nextDouble());
        publish();
    }
}

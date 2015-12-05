package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.FieldType;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.BooleanOption;
import io.firstwave.allium.api.options.FloatOption;
import io.firstwave.allium.api.options.IntegerOption;
import io.firstwave.allium.api.options.Options;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.io.PrintWriter;
import java.io.StringWriter;

import static io.firstwave.allium.demo.LayerUtils.gc;

/**
 * Created by obartley on 12/2/15.
 */
public class TroubleMaker extends Layer {

    @Inject(type = FieldType.OPTION)
    private final Layer derpderpderp = null;

    @Inject()
    private long delay;



    public TroubleMaker(int delay) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final RuntimeException re = new RuntimeException();
        re.printStackTrace(pw);
        setMessage(sw.toString());

        setOptions(Options.create()
                .add("delay", new IntegerOption(delay, 0, 60000))
                .add("\n", new BooleanOption(true))
                .add("\t⚙", new FloatOption(0.0f, -Float.MAX_VALUE, Float.MAX_VALUE))
                .build()
        );
    }

    @Override
    protected Canvas onCreateCanvas(RenderContext ctx) {
        return new Canvas(ctx.width / 2, ctx.height / 2);
    }

    @Override
    protected void onRender(RenderContext ctx) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gc(this).setFill(Color.PINK);
        gc(this).fillRect(0, 0, ctx.width / 2, ctx.height / 2);
        throw new RuntimeException();
    }
}

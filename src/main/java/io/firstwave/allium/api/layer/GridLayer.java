package io.firstwave.allium.api.layer;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.inject.Injector;
import io.firstwave.allium.api.options.FloatOption;
import io.firstwave.allium.api.options.IntegerOption;
import io.firstwave.allium.api.options.Options;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by obartley on 12/4/15.
 */
public class GridLayer extends Layer {

    @Inject
    private int minorInterval = 25;

    @Inject
    private int majorInterval = 4;

    private Color minorColor = Color.DARKGRAY.darker();
    private Color majorColor = Color.GRAY.darker();

//    @Inject
    private float minorStrokeWidth = 1;
//    @Inject
    private float majorStrikeWidth = 2;

    public GridLayer() {
        this(null);
    }

    public GridLayer(String name) {
        super(name);
        setOptions(new Options.Builder()
                .add("minorInterval", new IntegerOption(25, -1, 1000))
                .add("majorInterval", new IntegerOption(4, -1, 100))
                .add("minorStrokeWidth", new FloatOption(1f, 0f, 5f))
                .add("majorStrokeWidth", new FloatOption(2f, 0f, 5f))
                .build());


    }

    @Override
    protected void onPreRender(RenderContext ctx) {
        Injector.inject(this, this);
    }

    @Override
    protected void onRender(RenderContext ctx) {

        final GraphicsContext gc = getCanvas().getGraphicsContext2D();

        int xInt = 0;
        int yInt = 0;

        for (int y = 0; y < ctx.height; y += minorInterval) {
            for (int x = 0; x < ctx.width; x += minorInterval) {
                if (majorInterval > 0 && xInt++ > majorInterval) {
                    xInt = 0;
                    gc.setStroke(majorColor);
                    gc.setLineWidth(majorStrikeWidth);
                } else {
                    gc.setStroke(minorColor);
                    gc.setLineWidth(minorStrokeWidth);
                }
                gc.strokeLine(x, 0, x, ctx.height);
            }
            if (majorInterval > 0 && yInt++ > majorInterval) {
                yInt = 0;
                gc.setStroke(majorColor);
                gc.setLineWidth(majorStrikeWidth);
            } else {
                gc.setStroke(minorColor);
                gc.setLineWidth(minorStrokeWidth);
            }
            gc.strokeLine(0, y, ctx.width, y);
        }
    }
}

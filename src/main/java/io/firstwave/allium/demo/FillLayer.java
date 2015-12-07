package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.ColorOption;
import io.firstwave.allium.api.options.Options;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by obartley on 12/6/15.
 */
public class FillLayer extends Layer {

    @Inject
    Color color;

    public FillLayer() {
        this(null);
    }

    public FillLayer(String name) {
        super(name, Options.create().add("color", new ColorOption(Color.TRANSPARENT)).build());
    }

    @Override
    protected void onRender(RenderContext ctx) {
        final GraphicsContext gc = getCanvas().getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(0, 0, getScene().getWidth(), getScene().getHeight());
        publish();
    }
}

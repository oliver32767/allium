package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import javafx.scene.canvas.Canvas;

/**
 * Created by obartley on 12/2/15.
 */
public class TroubleMaker extends Layer {

    public TroubleMaker() {
        super("");
    }

    @Override
    protected Canvas onCreateCanvas(RenderContext ctx) {
        throw new RuntimeException("onCreateCanvas");
    }

    @Override
    protected void onPreRender(RenderContext ctx) {
        throw new RuntimeException("onPreRender");
    }

    @Override
    protected void onRender(RenderContext ctx) {
        throw new RuntimeException("onRender");
    }
}

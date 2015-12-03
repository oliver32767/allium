package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;

/**
 * Created by obartley on 12/2/15.
 */
public class LongRunningLayer extends Layer {

    public LongRunningLayer(String name) {
        super(name);
    }

    @Override
    protected void onRender(RenderContext ctx) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

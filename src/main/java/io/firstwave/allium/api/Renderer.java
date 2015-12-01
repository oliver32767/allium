package io.firstwave.allium.api;

import javafx.scene.canvas.Canvas;

/**
 * Created by obartley on 11/29/15.
 */
public interface Renderer {
    Canvas render(Layer layer);
}

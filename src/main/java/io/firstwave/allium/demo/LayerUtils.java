package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import javafx.scene.canvas.GraphicsContext;

/**
 * Created by obartley on 12/1/15.
 */
public class LayerUtils {

    public static GraphicsContext gc(Layer layer) {
        return layer.getCanvas().getGraphicsContext2D();
    }

}

package io.firstwave.allium.demo;

import io.firstwave.allium.core.Configuration;
import io.firstwave.allium.core.Layer;

/**
 * Created by obartley on 11/27/15.
 */
public class NoiseLayer extends Layer {

    public NoiseLayer() {
        super(new Configuration.Builder()
                .addOptionItem("simplex", "Use simplex noise", true)
                .build()
        );
    }
}

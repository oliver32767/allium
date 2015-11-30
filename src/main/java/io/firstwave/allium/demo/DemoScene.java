package io.firstwave.allium.demo;

import io.firstwave.allium.core.Configuration;
import io.firstwave.allium.core.Scene;
import javafx.scene.paint.Color;

/**
 * Created by obartley on 11/27/15.
 */
public class DemoScene extends Scene {

    @Override
    protected void onLoad() {
        setConfiguration(new Configuration.Builder()
                .addOptionItem("opt", true)
                .build());
        addLayer(new NoiseLayer(this, new Configuration.Builder()
                .addIntegerItem("r", 255)
                .addIntegerItem("g", 0)
                .addIntegerItem("b", 0)
                .build()
        )).setName("Red");
        addLayer(new NoiseLayer(this, new Configuration.Builder()
                .addIntegerItem("r", 0)
                .addIntegerItem("g", 255)
                .addIntegerItem("b", 0)
                .build()
        )).setName("Green");
        addLayer(new NoiseLayer(this, new Configuration.Builder()
                .addIntegerItem("r", 0)
                .addIntegerItem("g", 0)
                .addIntegerItem("b", 255)
                .build()
        )).setName("Blue").setVisibile(false);
        setBackgroundColor(Color.BLACK);
    }
}

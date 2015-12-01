package io.firstwave.allium.demo;

import io.firstwave.allium.api.Configuration;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.api.Visibility;
import javafx.scene.paint.Color;

/**
 * Created by obartley on 11/27/15.
 */
public class DemoScene extends Scene {



    @Override
    protected void onLoad() {
        setConfiguration(new Configuration.Builder()
                .addOptionItem("Option", true)
                .setDescription("Option", "Boolean option")
                .addOptionSetItem("Option Set", 1, "foo", "bar")
                .setDescription("Option Set", "Choose from a set of options")
                .addIntegerItem("Integer", 0)
                .addFloatItem("Float", 0)
                .addStringItem("String", null)
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
        )).setName("Blue").setVisibility(Visibility.GONE);

        setBackgroundColor(Color.BLACK);
    }
}

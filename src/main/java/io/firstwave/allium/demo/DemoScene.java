package io.firstwave.allium.demo;

import io.firstwave.allium.api.Configuration;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.api.Layer;
import javafx.scene.paint.Color;

/**
 * Created by obartley on 12/1/15.
 */
public class DemoScene extends Scene {

    @Override
    protected Layer onCreate() {
        final Layer root = new Layer();

        setBackgroundColor(Color.BLACK);

        root.setConfiguration(new Configuration.Builder()
                .addOptionItem("Option", true).build()
        );

        root.addChild(new Layer("foo"));
        root.addChild(new Layer("bar"));
        final Layer spam = new Layer("spam");
        spam.addChild(new Layer("eggs"));
        spam.addChild(new Layer());
        spam.addChild(new Layer());
        spam.addChild(new Layer());
        root.addChild(spam);
        return root;
    }


    @Override
    protected void onRender() {

    }
}

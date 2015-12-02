package io.firstwave.allium.demo;

import io.firstwave.allium.api.Configuration;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.api.Layer;
import javafx.scene.paint.Color;

import static io.firstwave.allium.api.utils.ConfigurationUtils.getOpt;
import static io.firstwave.allium.demo.LayerUtils.gc;

/**
 * Created by obartley on 12/1/15.
 */
public class DemoScene extends Scene {

    Layer foo, bar, spam, eggs;

    @Override
    protected Layer onCreate() {
        final Layer root = new Layer();

        setBackgroundColor(Color.BLACK);
        setWidth(1024);
        setHeight(1024);

        root.setConfiguration(new Configuration.Builder()
                .addOptionItem("Option", true)
                .addIntegerItem("Int", 128)
                .build()
        );

        foo =root.addChild(new Layer("foo"));
        bar =root.addChild(new Layer("bar"));
        spam = new Layer("spam");
        eggs = spam.addChild(new Layer("eggs"));

        spam.addChild(new Layer());
        spam.addChild(new Layer());
        spam.addChild(new Layer());
        root.addChild(spam);
        return root;
    }


    @Override
    protected void onRender(RenderContext ctx) {
        if (getOpt(getRoot(), "Option")) {
            gc(foo).setFill(Color.BLUE);
        } else {
            gc(foo).setFill(Color.PINK);
        }

        gc(foo).fillRect(750,750,1000,1000);
        foo.publish();

        gc(bar).setFill(Color.RED);
        gc(bar).fillRect(175,175,150,130);
        bar.publish();

        gc(spam).setFill(Color.YELLOW);
        gc(spam).fillRect(75,75,100,100);
        spam.publish();
    }
}

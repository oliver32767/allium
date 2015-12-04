package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.api.inject.FieldType;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.BooleanOption;
import io.firstwave.allium.api.options.Options;
import javafx.scene.paint.Color;

import static io.firstwave.allium.demo.LayerUtils.gc;

/**
 * Created by obartley on 12/1/15.
 */
public class DemoScene extends Scene {

    @Inject
    Layer foo;

    @Inject(key = "bar")
    Layer bar;

    @Inject(type = FieldType.LAYER, key = "spam")
    Layer spam;

    Layer eggs;

    @Override
    protected Layer onCreate() {
        final Layer root = new Layer();

        setBackgroundColor(Color.BLACK);
        setWidth(1024);
        setHeight(1024);

        root.setOptions(new Options.Builder()
                .add("Awesome", new BooleanOption(true))
                .build()
        );

        foo =root.addChild(new Layer("foo"));
        bar =root.addChild(new Layer("bar"));
        spam = new Layer("spam");
        eggs = spam.addChild(new Layer("eggs"));
        eggs.addChild(new TroubleMaker());
        spam.addChild(new Layer());
        spam.addChild(new Layer());
        spam.addChild(new Layer());
        root.addChild(spam);
        root.addChild(new NoiseLayer());
        return root;
    }


    @Override
    protected void onRender(RenderContext ctx) {
//        if (getOpt(getRoot(), "Option")) {
//            gc(foo).setFill(Color.BLUE);
//        } else {
//            gc(foo).setFill(Color.PINK);
//        }

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

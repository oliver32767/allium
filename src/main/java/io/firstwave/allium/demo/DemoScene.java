package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.api.inject.FieldType;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.inject.Injector;
import io.firstwave.allium.api.layer.GridLayer;
import javafx.scene.paint.Color;

import static io.firstwave.allium.demo.LayerUtils.gc;

/**
 * Created by obartley on 12/1/15.
 */
public class DemoScene extends Scene {

    @Inject(key = "foo")
    Layer foo;

    @Inject(key = "bar")
    Layer bar;

    @Inject(type = FieldType.LAYER, key = "spam")
    Layer spam;

    Layer eggs;

    @Inject Layer trouble;

    @Inject
    private float floaty;

    @Inject
    private boolean Awesome;

    @Override
    protected Layer onCreate() {
        final Layer root = new GridLayer();

        setBackgroundColor(Color.BLACK);
        setWidth(1024);
        setHeight(1024);

        foo = root.addChild(new Layer("foo"));
        foo.addChild(new RectLayer());
        foo.addChild(new RectLayer());
        bar = root.addChild(new Layer("bar"));
        spam = new Layer("spam");
        eggs = spam.addChild(new Layer("eggs"));
        eggs.addChild(new TroubleMaker());
        eggs.addChild(new RectLayer());
        eggs.addChild(new RectLayer());
        eggs.addChild(new RectLayer());
        eggs.addChild(new RectLayer());
        eggs.addChild(new RectLayer());
        spam.addChild(new RectLayer());
        spam.addChild(new RectLayer());
        spam.addChild(new RectLayer());
        spam.addChild(new Layer());
        spam.addChild(new Layer());
        spam.addChild(new Layer());

        Layer bbhh = new Layer();
        for (int i = 0; i < 60; i++) {
            bbhh.addChild(new RectLayer());
        }

        spam.addChild(bbhh);

        root.addChild(spam);
        root.addChild(new RectLayer());
        root.addChild(new NoiseLayer());

        Injector.inject(this, root);

        return root;
    }


    @Override
    protected void onRender(RenderContext ctx) {
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

package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.api.inject.FieldType;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.inject.Injector;
import io.firstwave.allium.api.layer.AnnotationLayer;
import io.firstwave.allium.api.layer.GridLayer;
import io.firstwave.allium.api.layer.NoiseLayer;
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

    @Inject AnnotationLayer anno;

    @Inject Color backgroundColor;


    @Override
    protected Layer onCreate() {
        final Layer root = new GridLayer();

        setWidth(1024);
        setHeight(1024);

        root.addChild(new NoiseLayer());
        foo = root.addChild(new Layer("foo"));
        foo.addChild(new RectLayer());
        foo.addChild(new RectLayer());
        bar = root.addChild(new Layer("bar"));
        spam = new Layer("spam");
        eggs = new Layer("eggs");
        eggs.addChild(new TroubleMaker());
        root.addChild(eggs);


        for (int i = 0; i < 10; i++) {
            spam.addChild(new RectLayer());
        }

        root.addChild(spam);
        root.addChild(new RectLayer());

        anno = new AnnotationLayer("anno");

        anno.addAnnotation(new AnnotationLayer.Annotation("Hello.", 512, 512));

        root.addChild(anno);




        return root;
    }

    @Override
    protected void onPreRender(RenderContext ctx) {
        super.onPreRender(ctx);
        Injector.inject(this, getRoot());
    }

    @Override
    protected void onRender(RenderContext ctx) {
        setBackgroundColor(Color.BLACK);

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

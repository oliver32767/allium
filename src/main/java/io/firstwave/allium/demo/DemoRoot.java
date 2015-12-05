package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.FieldType;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.inject.Injector;
import io.firstwave.allium.api.layer.AnnotationLayer;
import io.firstwave.allium.api.layer.GridLayer;
import io.firstwave.allium.api.layer.NoiseLayer;
import io.firstwave.allium.api.options.BooleanOption;
import io.firstwave.allium.api.options.ColorOption;
import io.firstwave.allium.api.options.Options;
import javafx.scene.paint.Color;
import org.pmw.tinylog.Logger;

import static io.firstwave.allium.demo.LayerUtils.gc;

/**
 * Created by obartley on 12/5/15.
 */
public class DemoRoot extends Layer {

    @Inject(key = "foo")
    Layer foo;

    @Inject(key = "bar")
    Layer bar;

    @Inject(type = FieldType.LAYER, key = "spam")
    Layer spam;

    Layer eggs;

    @Inject(key = "anno") AnnotationLayer anno;

    @Inject
    Color backgroundColor;

    public DemoRoot() {

        final Options opts = Options.create()
                .add("backgroundColor", new ColorOption(Color.BLACK))
                .build();

        setOptions(Options.buildUpon(opts)
                .addSeparator("Other stuff")
                .add("derp", new BooleanOption(true))
                .build()
        );

        final Layer root = new GridLayer();

        root.addChild(new NoiseLayer());
        foo = root.addChild(new Layer("foo"));
        foo.addChild(new RectLayer());
        foo.addChild(new RectLayer());
        bar = root.addChild(new Layer("bar"));
        spam = new Layer("spam");
        eggs = new Layer("eggs");
        eggs.addChild(new TroubleMaker(0));
        root.addChild(eggs);


        for (int i = 0; i < 10; i++) {
            spam.addChild(new RectLayer());
        }

        root.addChild(spam);
        anno = new AnnotationLayer("anno");

        root.addChild(anno);

        addChild(root);
    }


    @Override
    protected void onPreRender(RenderContext ctx) {
        super.onPreRender(ctx);
        Logger.warn(findChildByName("anno"));
        Injector.inject(this, this);
        anno.clearAnnotations();
        anno.addAnnotation(new AnnotationLayer.Annotation("Hello.", 512, 512));
        anno.addAnnotation(
                new AnnotationLayer.Annotation("No, over there", 512, 512)
                        .setOffset(-100, 100)
                        .setScale(0.5)
                        .setColor(Color.PINK)
        );
        anno.addAnnotation(new AnnotationLayer.Annotation("seed:" + ctx.seed, 128, 128));
    }

    @Override
    protected void onRender(RenderContext ctx) {
        getScene().setBackgroundColor(backgroundColor);

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

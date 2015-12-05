package io.firstwave.allium.api.layer;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.ColorOption;
import io.firstwave.allium.api.options.FloatOption;
import io.firstwave.allium.api.options.Options;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by obartley on 12/4/15.
 */
public class AnnotationLayer extends Layer {

    public static final double DEFAULT_SIZE = 13;

    @Inject
    private float textScale;

    @Inject
    Color textColor;

    private final List<Annotation> mAnnotationList = new ArrayList<>();

    public AnnotationLayer() {
        super();
    }

    public AnnotationLayer(String name) {
        super(name, Options.create()
                .add("textScale", new FloatOption(1.0f, 0.1f, 5.0f))
                .add("textColor", new ColorOption(Color.WHITE))
                .build()
        );
    }

    public void addAnnotation(Annotation annotation) {
        mAnnotationList.add(annotation);
    }



    @Override
    protected void onRender(RenderContext ctx) {
        final GraphicsContext gc = getCanvas().getGraphicsContext2D();

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(new Font(DEFAULT_SIZE * textScale));
        gc.setFill(textColor);

        for (Annotation annotation : mAnnotationList) {
            gc.fillText(
                    annotation.text,
                    annotation.x,
                    annotation.y
            );
        }
    }

    public static class Annotation {
        private final String text;
        private final double x;
        private final double y;

        public Annotation(String text, double x, double y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }
}

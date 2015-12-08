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

    public static final double DEFAULT_SIZE = 18;

    @Inject
    Color color;

    @Inject
    private float textScale;

    @Inject
    private float lineWidth;

    private final List<Annotation> mAnnotationList = new ArrayList<>();

    public AnnotationLayer() {
        super();
    }

    public AnnotationLayer(String name) {
        super(name, Options.create()
                .add("color", new ColorOption(Color.WHITE))
                .add("textScale", new FloatOption(1.0f, 0.1f, 5.0f))
                .add("lineWidth", new FloatOption(1.0f, 0.0f, 5.0f))
                .build()
        );
    }

    public void addAnnotation(Annotation annotation) {
        mAnnotationList.add(annotation);
    }

    public void clearAnnotations() {
        mAnnotationList.clear();
    }

    @Override
    protected void onPreRender(RenderContext ctx) {
        super.onPreRender(ctx);
        mAnnotationList.clear();
    }

    @Override
    protected void onRender(RenderContext ctx) {
        final GraphicsContext gc = getCanvas().getGraphicsContext2D();

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        gc.setFill(color);


        if (lineWidth > 0) {
            for (Annotation annotation : mAnnotationList) {
                gc.setFont(new Font(DEFAULT_SIZE * textScale * annotation.scale));
                if (annotation.offsetX != 0 && annotation.offsetY != 0) {
                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(lineWidth * annotation.scale);
                    gc.setLineDashes(null);
                    gc.strokeLine(annotation.x, annotation.y,
                            annotation.x + annotation.offsetX, annotation.y + annotation.offsetY);


                    if (annotation.color == null) {
                        gc.setStroke(color);
                    } else {
                        gc.setStroke(annotation.color);
                    }

                    gc.setLineDashes(lineWidth * 2 * textScale * annotation.scale,
                            lineWidth * 2 * textScale * annotation.scale);
                    gc.strokeLine(annotation.x, annotation.y,
                            annotation.x + annotation.offsetX, annotation.y + annotation.offsetY);
                }
            }
        }

        gc.setLineWidth(textScale);
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(null);
        for (Annotation annotation : mAnnotationList) {
            gc.setFont(new Font(DEFAULT_SIZE * textScale * annotation.scale));
            if (annotation.color == null) {
                gc.setFill(color);
            } else {
                gc.setFill(annotation.color);
            }
            gc.strokeText(
                    annotation.text,
                    annotation.x + annotation.offsetX,
                    annotation.y + annotation.offsetY
            );
            gc.fillText(
                    annotation.text,
                    annotation.x + annotation.offsetX,
                    annotation.y + annotation.offsetY
            );

        }
    }

    public static class Annotation {
        private final String text;
        private final double x;
        private final double y;

        private Color color = null;
        private double scale = 1;

        private double offsetX = 0;
        private double offsetY = 0;

        public Annotation(String text, double x, double y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }

        public Annotation setColor(Color c) {
            color = c;
            return this;
        }

        public Annotation setScale(double s) {
            scale = s;
            return this;
        }

        public Annotation setOffset(double x, double y) {
            offsetX = x;
            offsetY = y;
            return this;
        }


    }
}

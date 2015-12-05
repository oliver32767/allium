package io.firstwave.allium.api.layer;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by obartley on 12/4/15.
 */
public class AnnotationLayer extends Layer {

    private final List<Annotation> mAnnotationList = new ArrayList<>();

    public AnnotationLayer() {
        super();
    }

    public AnnotationLayer(String name) {
        super(name);
    }

    public void addAnnotation(Annotation annotation) {
        mAnnotationList.add(annotation);
    }

    @Override
    protected void onRender(RenderContext ctx) {
        final GraphicsContext gc = getCanvas().getGraphicsContext2D();

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

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

package io.firstwave.allium.demo2;

import io.firstwave.allium.api.Configuration;
import io.firstwave.allium.api2.Layer;
import io.firstwave.allium.api2.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * Created by obartley on 12/1/15.
 */
public class DemoScene2 extends Scene {

    @Override
    public void onLoad() {
        setConfiguration(new Configuration.Builder()
                .addOptionItem("Option", true)
                .setDescription("Option", "Boolean option")
                .addOptionSetItem("Option Set", 1, "foo", "bar")
                .setDescription("Option Set", "Choose from a set of options")
                .addIntegerItem("Integer", 0)
                .addFloatItem("Float", 0)
                .addStringItem("String", null)
                .build());

        putLayer("shapes", new Layer());
        putLayer("invisible", new Layer());
    }


    @Override
    protected void onRender() {
        drawShapes(getLayer("shapes").getCanvas().getGraphicsContext2D());
        getLayer("invisible").visibleProperty().set(false);
    }

    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }
}

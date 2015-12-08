package io.firstwave.allium.demo.riemann;

import javafx.scene.canvas.Canvas;

/**
 * Created by obartley on 12/8/15.
 */
public abstract class ShapeAdapter<S extends ShapeAdapter.Shape> {

    public abstract boolean isColliding(S shape, double x, double y, double area, int iteration);

    public abstract S onCreateShape(double x, double y, double area, int iteration);

    public abstract void onRender(S shape, Canvas canvas);


    public static class Shape {
        public final double x;
        public final double y;
        public final double area;
        public final int iteration;

        public Shape(double x, double y, double area, int iteration) {
            this.x = x;
            this.y = y;
            this.area = area;
            this.iteration = iteration;
        }
    }
}

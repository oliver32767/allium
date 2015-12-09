package io.firstwave.allium.demo.riemann;

/**
 * Created by obartley on 12/8/15.
 */
public abstract class ShapeAdapter<S extends ShapeAdapter.Shape> {

    public abstract boolean isOverlapping(RiemannLayer layer, S shape1, S shape2);

    public abstract S onCreateShape(RiemannLayer layer, double x, double y, double area, int iteration);

    public abstract void onRender(RiemannLayer layer, S shape);


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

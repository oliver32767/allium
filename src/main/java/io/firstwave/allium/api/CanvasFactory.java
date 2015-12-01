package io.firstwave.allium.api;


import javafx.scene.canvas.Canvas;

/**
 * Created by obartley on 11/30/15.
 */
public abstract class CanvasFactory {
    private final double mWidth;
    private final double mHeight;

    protected CanvasFactory(double width, double height) {
        mWidth = width;
        mHeight = height;
    }

    public final double getWidth() {
        return mWidth;
    }

    public final double getHeight() {
        return mHeight;
    }

    public final Canvas createCanvas() {
        final Canvas rv = onCreateCanvas();
        if (rv == null) {
            return new Canvas(mWidth, mHeight);
        }
        rv.setWidth(mWidth);
        rv.setHeight(mHeight);
        return rv;
    }

    protected abstract Canvas onCreateCanvas();
}

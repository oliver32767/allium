package io.firstwave.allium.api2;

/**
 * Created by obartley on 12/1/15.
 */
public abstract class Scene extends LayerGroup {

    private double mWidth = 0;
    private double mHeight = 0;

    public double getWidth() {
        return mWidth;
    }

    public void setWidth(double width) {
        mWidth = width;
    }

    public double getHeight() {
        return mHeight;
    }

    public void setHeight(double height) {
        mHeight = height;
    }

    public final void render() {
        render(getWidth(), getHeight());
    }

    public final void load() {
        onLoad();
    }

    public abstract void onLoad();

}

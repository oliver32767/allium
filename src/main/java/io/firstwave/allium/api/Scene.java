package io.firstwave.allium.api;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

/**
 * Created by obartley on 12/1/15.
 */
public abstract class Scene {
    
    private Layer mRoot;

    private double mWidth = 0;
    private double mHeight = 0;
    private SimpleObjectProperty<Color> mBackgroundColor = new SimpleObjectProperty<>(Color.TRANSPARENT);

    
    public final Layer getRoot() {
        return mRoot;
    }

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

    public Color getBackgroundColor() {
        return mBackgroundColor.getValue();
    }

    public void setBackgroundColor(Color color) {
        mBackgroundColor.setValue(color);
    }

    public ObservableValue<Color> backgroundColorProperty() {
        return mBackgroundColor;
    }

    public final void load() {
        mRoot = onCreate();
    }
    
    protected abstract Layer onCreate();

    public final void render() {
        
    }

    protected abstract void onRender();
}

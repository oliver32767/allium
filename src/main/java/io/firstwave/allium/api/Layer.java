package io.firstwave.allium.api;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;

/**
 * Created by obartley on 11/27/15.
 */
public abstract class Layer implements Configurable {

    private SimpleStringProperty mName = new SimpleStringProperty(toString());
    private SimpleObjectProperty<Visibility> mVisible = new SimpleObjectProperty<>(Visibility.VISIBLE);
    private SimpleBooleanProperty mRendering = new SimpleBooleanProperty(false);

    private final Configuration mConfiguration;
    private final Scene mScene;

    public Layer(Scene scene) {
        this(scene, null);
    }

    public Layer(Scene scene, Configuration configuration) {
        if (scene == null) {
            throw new NullPointerException();
        }
        mScene = scene;
        mConfiguration = configuration == null ? Configuration.EMPTY : configuration;
    }

    public Scene getScene() {
        return mScene;
    }

    @Override
    public final Configuration getConfiguration() {
        return mConfiguration;
    }

    public String getName() {
        return mName.getValue();
    }

    public void setName(String name) {
        if (name == null || name.trim().equals("")) {
            name = toString();
        }
        mName.setValue(name);
    }

    public Visibility getVisibility() {
        return mVisible.getValue();
    }

    public void setVisibility(Visibility visibility) {
        mVisible.setValue(visibility);
    }

    public ObservableValue<String> nameProperty() {
        return mName;
    }

    public ObjectProperty<Visibility> visibilityProperty() {
        return mVisible;
    }

    public ObservableValue<Boolean> renderingProperty() {
        return mRendering;
    }

    public final void render(Canvas canvas) {
        mRendering.setValue(true);
        onRender(canvas);
        mRendering.setValue(false);
    }

    protected abstract void onRender(Canvas canvas);
}

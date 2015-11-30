package io.firstwave.allium.core;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;

/**
 * Created by obartley on 11/27/15.
 */
public abstract class Layer implements Configurable {

    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VISIBLE = "visible";

    private SimpleStringProperty mName = new SimpleStringProperty(toString());
    private SimpleBooleanProperty mVisible = new SimpleBooleanProperty(true);

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

    public boolean isVisible() {
        return mVisible.getValue();
    }

    public void setVisible(boolean visible) {
        mVisible.setValue(visible);
    }

    public ObservableValue<String> nameProperty() {
        return mName;
    }

    public ObservableValue<Boolean> visibleProperty() {
        return mVisible;
    }

    public abstract Canvas render();
}

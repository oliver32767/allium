package io.firstwave.allium.ui.model;

import io.firstwave.allium.core.Layer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by obartley on 11/27/15.
 */
public class LayerRow {

    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VISIBLE = "visible";

    private final SimpleStringProperty name;
    private final SimpleBooleanProperty visible;

    public final Layer layer;

    public LayerRow(Layer layer, String label, boolean defaultVisibility) {
        this.layer = layer;
        name = new SimpleStringProperty(label);
        visible = new SimpleBooleanProperty(defaultVisibility);
    }

//    public String getName() {
//        return name.getValue();
//    }
//
//    public boolean isVisible() {
//        return visible.getValue();
//    }
//
//    public void setVisible(boolean visible) {
//        this.visible.setValue(visible);
//    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleBooleanProperty visibleProperty() {
        return visible;
    }

}

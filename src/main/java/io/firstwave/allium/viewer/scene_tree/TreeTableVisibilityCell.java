package io.firstwave.allium.viewer.scene_tree;

import io.firstwave.allium.api.Visibility;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;

/**
 * Created by obartley on 11/30/15.
 */
public class TreeTableVisibilityCell<S> extends TreeTableCell<S, ObjectProperty<Visibility>> {
    @Override
    protected void updateItem(final ObjectProperty<Visibility> item, boolean empty) {
        if (item == null || item.getValue() == Visibility.GONE) {
            setGraphic(null);
            return;
        }
        final CheckBox cb = new CheckBox();
        cb.setSelected(item.getValue() == Visibility.VISIBLE);
        cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                item.setValue(Visibility.VISIBLE);
            } else {
                item.setValue(Visibility.INVISIBLE);
            }
        });
        setGraphic(cb);
    }
}

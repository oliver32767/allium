package io.firstwave.allium.viewer.scene_tree;

import io.firstwave.allium.api.Visibility;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by obartley on 11/30/15.
 */
public class SceneItem {

    private final ReadOnlyStringProperty mNameProperty;
    private final ObjectProperty<Visibility> mVisibleProperty;

    public SceneItem(String name) {
        this(name, null);
    }

    public SceneItem(String name, ObjectProperty<Visibility> visibility) {
        mNameProperty = new SimpleStringProperty(name);
        if (visibility == null) {
            mVisibleProperty = new ReadOnlyObjectWrapper<>(Visibility.GONE);
        } else {
            mVisibleProperty = visibility;
        }
    }

    public ReadOnlyStringProperty nameProperty() {
        return mNameProperty;
    }

    public ObjectProperty<Visibility> visibleProperty() {
        return mVisibleProperty;
    }
}

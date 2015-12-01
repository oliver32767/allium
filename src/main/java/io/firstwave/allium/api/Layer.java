package io.firstwave.allium.api;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by obartley on 12/1/15.
 */
public class Layer {

    private final ObservableList<Layer> mChildNodes = FXCollections.observableArrayList();

    private Configuration mConfiguration;
    private Layer mParent;

    private final SimpleStringProperty mName = new SimpleStringProperty();
    private final SimpleBooleanProperty mVisible = new SimpleBooleanProperty(true);

    public Layer() {
        this(null, null);
    }

    public Layer(String name) {
        this(name, null);
    }

    public Layer(String name, Configuration configuration) {
        setName(name);
        setConfiguration(configuration);
        mVisible.addListener((observable, oldValue, newValue) -> updateChildVisibility(newValue));
    }

    public final Configuration getConfiguration() {
        return mConfiguration;
    }

    public final void setConfiguration(Configuration configuration) {
        if (configuration == null) {
            configuration = Configuration.EMPTY;
        }
        mConfiguration = configuration;
    }

    public String getName() {
        return mName.getValue();
    }

    public void setName(String name) {
        if (name == null) {
            name = getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
        }
        mName.setValue(name);
    }

    public ObservableValue<String> nameProperty() {
        return mName;
    }

    public boolean isVisible() {
        return mVisible.getValue();
    }

    public void setVisible(boolean visible) {
        mVisible.setValue(visible);
    }

    private void updateChildVisibility(boolean visible) {
        for (Layer child : mChildNodes) {
            child.setVisible(visible);
        }
    }

    public BooleanProperty visibleProperty() {
        return mVisible;
    }

    // CHILD NODE API /////////////////////////////////////////////////////////////////////////////////////////////////

    public final ObservableList<Layer> getChildNodes() {
        return FXCollections.unmodifiableObservableList(mChildNodes);
    }

    public final int getChildCount() {
        return mChildNodes.size();
    }

    public final Layer getChildAt(int i) {
        return mChildNodes.get(i);
    }

    public final Layer findChildByName(String name) {
        if (name == null) {
            return null;
        }

        if (name.equals(mName.getValue())) {
            return this;
        }

        for (Layer child : mChildNodes) {
            child = child.findChildByName(name);
            if (child != null) {
                return child;
            }
        }
        return null;
    }


    public Layer addChild(Layer child) {
        child.removeFromParent();
        child.mParent = this;
        mChildNodes.add(child);
        return child;
    }

    public final void removeChild(Layer child) {
        final int index = indexOf(child);
        if (index >= 0) {
            removeChildInternal(index);
        }
    }

    public final void removeAllChildren() {
        for (int i = mChildNodes.size() - 1; i >= 0; i--) {
            removeChildInternal(i);
        }
    }

    private void removeChildInternal(int index) {
        mChildNodes.get(index).mParent = null;
        mChildNodes.remove(index);
    }

    public final int indexOf(Layer child) {
        return mChildNodes.indexOf(child);
    }

    public final void removeFromParent() {
        if (mParent == null) {
            return;
        }
        mParent.removeChild(this);
    }

    // RENDER API //////////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO

}

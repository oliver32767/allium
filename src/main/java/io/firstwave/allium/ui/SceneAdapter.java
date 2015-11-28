package io.firstwave.allium.ui;

import io.firstwave.allium.core.Scene;
import io.firstwave.allium.ui.model.LayerRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Created by obartley on 11/27/15.
 */
public class SceneAdapter implements Scene.Observer {
    private final ObservableList<LayerRow> mRowList = FXCollections.observableArrayList(new ArrayList<LayerRow>());
    private Scene mScene;

    public SceneAdapter() {
        this(null);
    }
    public SceneAdapter(Scene scene) {
        setScene(scene);
    }

    public void setScene(Scene scene) {
        if (mScene != null) {
            if (mScene.equals(scene)) {
                return;
            } else {
                mScene.setObserver(null);
            }
        }
        mScene = scene;
        if (mScene != null) {
            mScene.setObserver(this);
            onSceneChanged(mScene);
        }
    }

    public ObservableList<LayerRow> getLayerRowList() {
        return mRowList;
    }

    @Override
    public void onSceneChanged(Scene scene) {
        mRowList.clear();
        final int count = scene.getLayerCount();
        for (int i = 0; i < count; i++) {
            mRowList.add(new LayerRow(
                    scene.getLayerAt(i),
                    scene.getLabelForLayer(i),
                    scene.getDefaultVisibilityForLayer(i)
            ));
        }
    }
}

package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.Scene;

/**
 * Created by obartley on 12/5/15.
 */
public class GameScene extends Scene {
    @Override
    protected Layer onCreate() {
        setWidth(1024);
        setHeight(1024);
        return new GameRoot();
    }
}

package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.Scene;

/**
 * Created by obartley on 12/1/15.
 */
public class DemoScene extends Scene {

    @Override
    protected Layer onCreate() {
        setWidth(1024);
        setHeight(1024);
        return new DemoRoot();
    }

}

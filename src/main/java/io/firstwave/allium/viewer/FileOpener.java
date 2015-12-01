package io.firstwave.allium.viewer;

import io.firstwave.allium.api.Scene;
import io.firstwave.allium.demo.DemoScene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by obartley on 11/30/15.
 */
public class FileOpener {
    public static List<Class<? extends Scene>> getScenes(File file) {
        final List<Class<? extends Scene>> rv = new ArrayList<Class<? extends Scene>>();
        if (file == null) {
            return rv;
        }

        rv.add(DemoScene.class);
        return rv;
    }
}

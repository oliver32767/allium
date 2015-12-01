package io.firstwave.allium;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Created by obartley on 11/30/15.
 */
public class Prefs {

    public static File getLastPath() {
        Preferences prefs = Preferences.userNodeForPackage(Allium.class);
        String filePath = prefs.get("lastPath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public static void setLastPath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(Allium.class);
        if (file != null) {
            prefs.put("lastPath", file.getPath());
        } else {
            prefs.remove("lastPath");
        }
    }
}

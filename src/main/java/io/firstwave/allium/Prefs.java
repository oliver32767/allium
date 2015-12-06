package io.firstwave.allium;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Created by obartley on 11/30/15.
 */
public class Prefs {

    public static boolean isDarkTheme() {
        Preferences prefs = Preferences.userNodeForPackage(Allium.class);
        return prefs.getBoolean("darkTheme", false);
    }

    public static void setDarkTheme(boolean darkTheme) {
        Preferences prefs = Preferences.userNodeForPackage(Allium.class);
        prefs.putBoolean("darkTheme", darkTheme);
    }

    public static File getLastPath() {
        Preferences prefs = Preferences.userNodeForPackage(Allium.class);
        String filePath = prefs.get("lastPath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public static void setLastPath(File lastPath) {
        Preferences prefs = Preferences.userNodeForPackage(Allium.class);
        if (lastPath != null) {
            prefs.put("lastPath", lastPath.getPath());
        } else {
            prefs.remove("lastPath");
        }
    }
}

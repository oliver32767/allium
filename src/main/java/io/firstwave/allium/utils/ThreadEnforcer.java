package io.firstwave.allium.utils;

import javafx.application.Platform;

/**
 * Created by obartley on 12/1/15.
 */
public interface ThreadEnforcer {

    void enforce();

    ThreadEnforcer ANY = () -> {
        // i'll allow it
    };

    ThreadEnforcer MAIN = () -> {
        if (!Platform.isFxApplicationThread())
            throw new IllegalStateException("Method cannot be accessed from non-main thread");
    };

    ThreadEnforcer BACKGROUND = () -> {
        if (Platform.isFxApplicationThread())
            throw new IllegalStateException("Method cannot be accessed from main thread");
    };
}

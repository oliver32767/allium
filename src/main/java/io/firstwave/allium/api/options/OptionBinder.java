package io.firstwave.allium.api.options;

import javafx.scene.Node;

/**
 * Created by obartley on 12/1/15.
 */
public interface OptionBinder {
    Node bind(final Options options, final String key);
}

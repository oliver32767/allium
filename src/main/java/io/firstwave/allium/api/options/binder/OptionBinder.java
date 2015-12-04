package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.Options;
import javafx.scene.Node;

/**
 * Created by obartley on 12/1/15.
 */
public interface OptionBinder {
    Node bind(final String key, final Options options);
}

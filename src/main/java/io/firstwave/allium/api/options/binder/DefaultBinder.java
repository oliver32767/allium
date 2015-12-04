package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.Options;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * Created by obartley on 12/3/15.
 */
public class DefaultBinder implements OptionBinder {

    @Override
    public Node bind(String key, Options options) {
        return new TextField("No binder for key: " + key + " (type: " + options.getOptionType(key) + ")");
    }
}

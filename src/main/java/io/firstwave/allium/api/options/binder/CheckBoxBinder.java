package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.Options;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

/**
 * Created by obartley on 12/2/15.
 */
public class CheckBoxBinder implements OptionBinder {

    @Override
    public Node bind(final String key, final Options options) {
        final CheckBox rv = new CheckBox(key);
        rv.setSelected(options.get(Boolean.class, key));
        rv.setOnAction(event -> {
            options.edit().set(Boolean.class, key, rv.isSelected());
        });
        return rv;
    }
}

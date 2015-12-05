package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.Option;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

/**
 * Created by obartley on 12/2/15.
 */
public class CheckBoxBinder extends OptionBinder {

    @Override
    public Node bind(Option option) {
        if (option.getValueType() != Boolean.class) {
            return null;
        }
        final CheckBox rv = new CheckBox(option.getKey());
        rv.setOnAction(event -> option.getEditor().setBoolean(rv.isSelected()));
        return rv;
    }

    @Override
    public void update(Node node, Option option) {
        ((CheckBox) node).setSelected(option.getBoolean());
    }
}

package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.Option;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * Created by obartley on 12/3/15.
 */
public class DefaultBinder extends OptionBinder {

    @Override
    public Node bind(Option option) {
        return new TextField("No binder for key: " + option.getKey() + " (type: " + option.getClass() + ")");
    }

    @Override
    public void update(Node node, Option option) {

    }
}

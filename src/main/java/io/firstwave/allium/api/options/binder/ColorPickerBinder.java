package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.Options;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.pmw.tinylog.Logger;

/**
 * Created by obartley on 12/5/15.
 */
public class ColorPickerBinder implements OptionBinder {
    @Override
    public Node bind(final String key, final Options options) {
        final HBox rv = new HBox();
        rv.setAlignment(Pos.CENTER_LEFT);

        final Label lbl = new Label(key);
        rv.getChildren().add(lbl);
        final ColorPicker cp = new ColorPicker(options.getValue(Color.class, key));
        cp.valueProperty().addListener((observable, oldValue, newValue) -> {
            Logger.warn(newValue);
            options.edit().set(Color.class, key, newValue);
        });
        rv.getChildren().add(cp);


        return rv;
    }

    @Override
    public void updateValue(Node node, String key, Options options) {
        Pane p = (Pane) node;
        ColorPicker cp = (ColorPicker) p.getChildren().get(1);
        cp.setValue(options.getValue(Color.class, key));
    }
}

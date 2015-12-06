package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.Option;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Created by obartley on 12/5/15.
 */
public class ColorPickerBinder extends OptionBinder {

    @Override
    public Node bind(Option option) {
        if (option.getValueType() != Color.class) {
            return null;
        }

        final HBox rv = new HBox();
        rv.setAlignment(Pos.CENTER_LEFT);
        rv.setSpacing(5);

        final Label lbl = new Label(option.getKey());
        rv.getChildren().add(lbl);
        final ColorPicker cp = new ColorPicker((Color) option.get());
        cp.setMinHeight(26);
        cp.valueProperty().addListener((observable, oldValue, newValue) -> {
            option.getEditor().set(newValue);
        });
        rv.getChildren().add(cp);

        return rv;
    }

    @Override
    public void update(Node node, Option option) {
        Pane p = (Pane) node;
        ColorPicker cp = (ColorPicker) p.getChildren().get(1);
        cp.setValue((Color) option.get());
    }
}

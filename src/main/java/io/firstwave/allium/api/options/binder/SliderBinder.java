package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.FloatOption;
import io.firstwave.allium.api.options.IntegerOption;
import io.firstwave.allium.api.options.Option;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Created by obartley on 12/4/15.
 */
public class SliderBinder extends OptionBinder {

    @Override
    public Node bind(Option option) {
        final Slider sl;
        final Label lb = new Label(option.getKey());

        if (option instanceof IntegerOption) {
            final IntegerOption io = (IntegerOption) option;
            sl = new Slider(io.min, io.max, option.getInt());

            sl.valueProperty().addListener((observable, oldValue, newValue) -> {
                option.getEditor().setInt((int) sl.getValue());
                lb.setText(option.getKey() + ": " + (int) sl.getValue());
            });

        } else if (option instanceof FloatOption) {
            final FloatOption io = (FloatOption) option;
            sl = new Slider(io.min, io.max, option.getFloat());

            sl.valueProperty().addListener((observable, oldValue, newValue) -> {
                option.getEditor().setFloat((float) sl.getValue());
                lb.setText(option.getKey() + ": " + (float) sl.getValue());
            });

        } else {
            return null;
        }

        return new VBox(lb, sl);
    }

    @Override
    public void update(Node node, Option option) {
        final Pane pane = (Pane) node;

        if (option instanceof IntegerOption) {
            ((Slider) pane.getChildren().get(1)).setValue(option.getInt());
            ((Label) pane.getChildren().get(0)).setText(option.getKey() + ": " + option.getInt());
        } else if (option instanceof FloatOption) {
            ((Slider) pane.getChildren().get(1)).setValue(option.getFloat());
            ((Label) pane.getChildren().get(0)).setText(option.getKey() + ": " + option.getFloat());
        }
    }
}

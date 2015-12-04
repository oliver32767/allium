package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.FloatOption;
import io.firstwave.allium.api.options.IntegerOption;
import io.firstwave.allium.api.options.Option;
import io.firstwave.allium.api.options.Options;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Created by obartley on 12/4/15.
 */
public class SliderBinder implements OptionBinder {


    @Override
    public Node bind(String key, Options options) {
        final Option opt = options.getOption(key);

        final Slider sl;
        final Label lb = new Label(key);

        if (opt instanceof IntegerOption) {
            final IntegerOption io = (IntegerOption) opt;
            sl = new Slider(io.min, io.max, (int) options.getValue(key));

            sl.valueProperty().addListener((observable, oldValue, newValue) -> {
                options.edit().set(Integer.class, key, (int) sl.getValue());
                lb.setText(key + ": " + (int) sl.getValue());
            });

        } else if (opt instanceof FloatOption) {
            final FloatOption io = (FloatOption) opt;
            sl = new Slider(io.min, io.max, (float) options.getValue(key));

            sl.valueProperty().addListener((observable, oldValue, newValue) -> {
                options.edit().set(Float.class, key, (float) sl.getValue());
                lb.setText(key + ": " + (float) sl.getValue());
            });

        } else {
            return null;
        }

        return new VBox(lb, sl);
    }

    @Override
    public void updateValue(Node node, String key, Options options) {
        final Pane pane = (Pane) node;

        ((Label) pane.getChildren().get(0)).setText(key + ": " + options.getValue(key));

        final Option opt = options.getOption(key);

        if (opt instanceof IntegerOption) {
            ((Slider) pane.getChildren().get(1)).setValue((int) options.getValue(key));
        } else if (opt instanceof FloatOption) {
            ((Slider) pane.getChildren().get(1)).setValue((float) options.getValue(key));
        }
    }
}

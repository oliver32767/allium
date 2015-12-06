package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.Option;
import io.firstwave.allium.api.options.SingleChoiceOption;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;


/**
 * Created by obartley on 12/5/15.
 */
public class ChoiceBoxBinder extends OptionBinder {
    @Override
    public Node bind(final Option option) {
        if (!(option instanceof SingleChoiceOption)) {
            return null;
        }

        final HBox rv = new HBox(new Label(option.getKey()));
        rv.setAlignment(Pos.CENTER_LEFT);
        rv.setSpacing(5);

        final ChoiceBox<String> cb = new ChoiceBox<>();
        for (String s : ((SingleChoiceOption) option).options) {
            cb.getItems().add(s);
        }

        cb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            option.getEditor().setString(newValue);
        });

        rv.getChildren().add(cb);

        return rv;
    }

    @Override
    public void update(Node node, Option option) {
        ChoiceBox cb = (ChoiceBox) ((Pane) node).getChildren().get(1);
        cb.getSelectionModel().select(((SingleChoiceOption) option).indexOfValue());
    }
}

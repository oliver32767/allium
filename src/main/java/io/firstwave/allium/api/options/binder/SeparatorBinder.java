package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.Option;
import io.firstwave.allium.api.options.Separator;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by obartley on 12/5/15.
 */
public class SeparatorBinder extends OptionBinder {

    @Override
    public Node bind(Option option) {
        if (!(option instanceof Separator)) {
            return null;
        }
        final Label lbl = new Label();
        Font f = lbl.getFont();
        lbl.setFont(Font.font(f.getFamily(), FontWeight.BOLD, f.getSize()));

        final HBox rv = new HBox(lbl);
        rv.setSpacing(5);

        javafx.scene.control.Separator sep = new javafx.scene.control.Separator(Orientation.HORIZONTAL);
        rv.setAlignment(Pos.CENTER_LEFT);
        rv.getChildren().add(sep);

        HBox.setHgrow(sep, Priority.ALWAYS);

        return rv;
    }

    @Override
    public void update(Node node, Option option) {
        final String label = ((Separator) option).label;
        if (label == null) {
            ((HBox) node).getChildren().get(0).setVisible(false);
        } else {
            ((Label) ((HBox) node).getChildren().get(0)).setText(label);
        }
    }
}

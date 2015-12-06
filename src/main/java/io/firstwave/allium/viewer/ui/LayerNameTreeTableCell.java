package io.firstwave.allium.viewer.ui;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderState;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Callback;

/**
 * Created by obartley on 12/4/15.
 */
public class LayerNameTreeTableCell extends TextFieldTreeTableCell<Layer, Layer> {

    public static Callback<TreeTableColumn<Layer, Layer>, TreeTableCell<Layer, Layer>> getFactory() {
        return param -> new LayerNameTreeTableCell();
    }


    private ChangeListener<RenderState> mLayerStateChangeListener;
    private Layer mCurrent;


    @Override
    public void updateItem(final Layer item, boolean empty) {
        if (mCurrent != null) {
            mCurrent.stateProperty().removeListener(mLayerStateChangeListener);
        }
        mCurrent = item;
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            updateItemBadge(this, item);
            final TextFieldTreeTableCell cell = this;
            mLayerStateChangeListener = (observable, oldValue, newValue) -> {
                updateItemBadge(cell, item);
            };
            item.stateProperty().addListener(mLayerStateChangeListener);
        }

    }

    private void updateItemBadge(TextFieldTreeTableCell cell, Layer item) {
        Color c;
        switch (item.stateProperty().getValue()) {
            case RENDERING:
                c = Color.YELLOW;
                break;
            case PUBLISHED:
                c = Color.GREEN;
                break;
            case ERROR:
                c = Color.RED;
                break;
            default:
                c = Color.GRAY;
        }
        if (cell.getGraphic() == null) {
            final Circle circ = new Circle(2);
            circ.setStrokeWidth(1);
            circ.setStrokeType(StrokeType.OUTSIDE);
            cell.setGraphic(circ);
        }
        ((Circle) cell.getGraphic()).setFill(c);
        ((Circle) cell.getGraphic()).setStroke(c.brighter());
    }
}

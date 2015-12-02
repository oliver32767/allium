package io.firstwave.allium.utils;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

/**
 * Created by obartley on 11/27/15.
 */
public class FXUtils {

    public static void runOnMainThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    public static void disableColumnReordering(final TableView<?> tableView) {
        tableView.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                final TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });
    }

    public static void hideColumnHeaders(final TableView<?> table) {
        Pane header = (Pane) table.lookup("TableHeaderRow");
        header.setVisible(false);
        table.setLayoutY(-header.getHeight());
        table.autosize();
    }
}

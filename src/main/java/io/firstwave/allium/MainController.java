package io.firstwave.allium;

import io.firstwave.allium.core.Layer;
import io.firstwave.allium.core.Scene;
import io.firstwave.allium.demo.DemoScene;
import io.firstwave.allium.ui.SceneAdapter;
import io.firstwave.allium.ui.model.LayerRow;
import io.firstwave.allium.ui.util.ControlUtils;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pmw.tinylog.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TableView<LayerRow> layerList;

    @FXML
    private TableColumn<Layer, String> layerName;

    @FXML
    private TableColumn<Layer, Boolean> layerVisibility;

    @FXML
    private VBox configurationList;

    private final SceneAdapter mSceneAdapter = new SceneAdapter();
    private Stage mStage;

    public void setStage(Stage stage) {
        mStage = stage;
    }

    public Stage getStage() {
        return mStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControlUtils.disableColumnReordering(layerList);
        layerList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        layerVisibility.setEditable(true);
        layerList.setEditable(true);

        layerVisibility.setCellValueFactory(new PropertyValueFactory<Layer, Boolean>(LayerRow.PROPERTY_VISIBLE));
        layerVisibility.setCellFactory(CheckBoxTableCell.forTableColumn(layerVisibility));

        layerName.setCellValueFactory(new PropertyValueFactory<Layer, String>(LayerRow.PROPERTY_NAME));
        layerList.setItems(mSceneAdapter.getLayerRowList());
        layerList.getSelectionModel().getSelectedCells().addListener(new ListChangeListener<TablePosition>() {
            @Override
            public void onChanged(Change<? extends TablePosition> c) {
                if (layerList.getSelectionModel().getSelectedCells().size() > 0) {
                    updateConfigurationList(
                            layerList.getSelectionModel().getSelectedCells().get(0).getRow());
                } else {
                    updateConfigurationList(-1);
                }
            }
        });
        setScene(new DemoScene());
    }

    private void setScene(Scene scene) {
        mSceneAdapter.setScene(scene);
    }

    private void updateConfigurationList(int index) {
        if (index < 0 || index >= mSceneAdapter.getLayerRowList().size()) {
            configurationList.getChildren().clear();
            return;
        }

        final LayerRow layer = mSceneAdapter.getLayerRowList().get(index);

        Logger.debug(layer);


    }
}

package io.firstwave.allium;

import io.firstwave.allium.core.Configuration;
import io.firstwave.allium.core.Layer;
import io.firstwave.allium.core.Scene;
import io.firstwave.allium.demo.DemoScene;
import io.firstwave.allium.ui.util.ControlUtils;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pmw.tinylog.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TableView<Layer> layerList;

    @FXML
    private TableColumn<Layer, String> layerName;

    @FXML
    private TableColumn<Layer, Boolean> layerVisibility;

    @FXML
    private VBox configurationList;

    @FXML
    private StackPane layerStack;

    private Stage mStage;
    private Scene mScene;

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

        layerVisibility.setCellValueFactory(new PropertyValueFactory<Layer, Boolean>(Layer.PROPERTY_VISIBLE));
        layerVisibility.setCellFactory(CheckBoxTableCell.forTableColumn(layerVisibility));

        layerName.setCellValueFactory(new PropertyValueFactory<Layer, String>(Layer.PROPERTY_NAME));

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
        scene.load();
        mScene = scene;
        layerList.setItems(scene.getLayerList());
    }

    private void updateConfigurationList(int index) {
        configurationList.getChildren().clear();
        if (index < 0 || index >= mScene.getLayerList().size()) {
            return;
        }

        final Layer layer = mScene.getLayerList().get(index);
        layerStack.getChildren().add(layer.render());

        final Configuration config = layer.getConfiguration();

        for (String key : config.keySet()) {
            final Label lbl = new Label(key + ": " + config.getValue(key));
            configurationList.getChildren().add(lbl);
        }

        Logger.debug(layer);
    }
}

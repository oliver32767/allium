package io.firstwave.allium;

import io.firstwave.allium.core.Configuration;
import io.firstwave.allium.core.Layer;
import io.firstwave.allium.core.Renderer;
import io.firstwave.allium.core.Scene;
import io.firstwave.allium.demo.DemoScene;
import io.firstwave.allium.ui.util.ControlUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.pmw.tinylog.Logger;

import java.net.URL;
import java.util.*;

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

    @FXML
    private ScrollPane scrollPane;

    private final Map<Layer, Canvas> mLayerCanvasMap = new LinkedHashMap<Layer, Canvas>();

    private final SimpleBooleanProperty mIsRendering = new SimpleBooleanProperty(false);

    private Stage mStage;
    private Scene mScene;

    public void setStage(Stage stage) {
        mStage = stage;
        updateTitle();
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

//        layerList.disableProperty().bind(mIsRendering);
        configurationList.disableProperty().bind(mIsRendering);

        setScene(new DemoScene());
    }

    private void updateTitle() {
        if (mStage == null) {
            return;
        }
        if (mScene == null) {
            mStage.setTitle(Const.APP_NAME + " (" + Const.VERSION + ")");
        } else {
            mStage.setTitle(Const.APP_NAME + " (" + Const.VERSION + ") - " + mScene.toString());
        }
    }

    private void setScene(Scene scene) {
        scene.load();
        updateTitle();

        for (Node node : layerStack.getChildren()) {
            node.visibleProperty().unbind();
        }

        mLayerCanvasMap.clear();
        mScene = scene;
        layerList.setItems(scene.getLayerList());

        render();
    }

    private void render() {
        layerStack.getChildren().clear();

        if (mScene == null) {
            setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        setBackgroundColor(mScene.getBackgroundColor());

        final Renderer renderer = mScene.createRenderer();
        final List<Layer> layers = new ArrayList<Layer>(mScene.getLayerList());

        Logger.debug("rendering...");
        final Task<Void> renderTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                mIsRendering.setValue(true);
                for (Layer l : layers) {
                    final Canvas canvas;
                    final Layer layer = l;
                    try {
                        Logger.debug("rendering:" + layer.getName());
                        canvas = renderer.render(layer);
                        canvas.visibleProperty().bind(layer.visibleProperty());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mLayerCanvasMap.put(layer, canvas);
                                layerStack.getChildren().add(canvas);
                            }
                        });
                    } catch (Exception e) {
                        Logger.warn(e);
                    }
                }
                mIsRendering.setValue(false);
                return null;
            }
        };
        Thread th = new Thread(renderTask);
        th.setDaemon(true);
        th.start();
    }

    private void setBackgroundColor(Color color) {
        final int green = (int) (color.getGreen()*255);
        final String greenString = Integer.toHexString(green);
        final int red = (int) (color.getRed()*255);
        final String redString = Integer.toHexString(red);
        final int blue = (int) (color.getBlue()*255);
        final String blueString = Integer.toHexString(blue);

        String hexColor = "#"+redString+greenString+blueString;

        scrollPane.setStyle("-fx-background: " + hexColor);
    }


    private void updateConfigurationList(int index) {
        configurationList.getChildren().clear();
        if (index < 0 || index >= mScene.getLayerList().size()) {
            return;
        }

        final Layer layer = mScene.getLayerList().get(index);
        final Configuration config = layer.getConfiguration();

        for (String key : config.keySet()) {
            final Label lbl = new Label(key + ": " + config.getValue(key));
            configurationList.getChildren().add(lbl);
        }

        Logger.debug(layer);
    }
}

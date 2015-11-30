package io.firstwave.allium;

import io.firstwave.allium.core.Configuration;
import io.firstwave.allium.core.Layer;
import io.firstwave.allium.core.Renderer;
import io.firstwave.allium.core.Scene;
import io.firstwave.allium.demo.DemoScene;
import io.firstwave.allium.ui.util.ConfigurationController;
import io.firstwave.allium.ui.util.ControlUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    private static final double SCALE_DELTA = 1.1;

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

    @FXML
    private MenuItem menuZoomIn;

    @FXML
    private MenuItem menuZoomOut;

    @FXML MenuItem menuNoZoom;

    @FXML
    private MenuItem menuRender;

    @FXML
    private MenuItem menuReload;

    @FXML
    private MenuItem menuQuit;

    @FXML
    private MenuItem menuAbout;

    @FXML
    private Label statusLabel;

    @FXML
    private Button applyConfiguration;

    @FXML
    private Button cancelConfiguration;

    @FXML
    private Button sceneConfiguration;

    private final Map<Layer, Canvas> mLayerCanvasMap = new LinkedHashMap<Layer, Canvas>();

    private final SimpleBooleanProperty mIsRendering = new SimpleBooleanProperty(false);

    private ConfigurationController mConfigurationController;
    private Stage mStage;
    private Scene mScene;
    private Class<? extends Scene> mSceneType;

    private Configuration.OnConfigurationChangedListener mOnConfigurationChangedListener;

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

        configurationList.disableProperty().bind(mIsRendering);

        menuReload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reload();
            }
        });

        final EventHandler<ActionEvent> zoom = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final MenuItem mnu = (MenuItem) event.getSource();
                zoom((Integer) mnu.getUserData());
            }
        };
        menuZoomIn.setUserData(1);
        menuZoomIn.setOnAction(zoom);
        menuZoomOut.setUserData(-1);
        menuZoomOut.setOnAction(zoom);
        menuNoZoom.setUserData(0);
        menuNoZoom.setOnAction(zoom);

        menuQuit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (getStage() != null) {
                    getStage().close();
                }
            }
        });

        menuRender.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!menuRender.isDisable()) {
                    render();
                }
            }
        });
        menuRender.disableProperty().bind(mIsRendering);

        menuAbout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("About");
                alert.setHeaderText(Const.APP_NAME + " (" + Const.VERSION + ")");
                alert.setContentText(Const.COPYRIGHT);

                alert.showAndWait();
            }
        });

        mConfigurationController = new ConfigurationController(configurationList);
        applyConfiguration.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setStatus("Applied " + mConfigurationController.apply() + " change(s)");
            }
        });
        cancelConfiguration.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mConfigurationController.cancel();
            }
        });
        sceneConfiguration.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                layerList.getSelectionModel().clearSelection();
            }
        });

        mConfigurationController.configurationProperty().addListener(new ChangeListener<Configuration>() {
            @Override
            public void changed(ObservableValue<? extends Configuration> observable, Configuration oldValue, Configuration newValue) {
                applyConfiguration.disableProperty().bind(newValue.unchangedProperty());
                cancelConfiguration.disableProperty().bind(newValue.unchangedProperty());
            }
        });

        setSceneType(DemoScene.class);
    }

    private void updateTitle() {
        if (mStage == null) {
            return;
        }
        if (mScene == null) {
            mStage.setTitle(Const.APP_NAME + " (" + Const.VERSION + ")");
        } else {
            mStage.setTitle(mScene.toString() + " - " + Const.APP_NAME + " (" + Const.VERSION + ")");
        }
    }

    private void setSceneType(Class<? extends Scene> sceneType) {
        mSceneType = sceneType;
        reload();
    }

    private void reload() {
        for (Node node : layerStack.getChildren()) {
            node.visibleProperty().unbind();
        }

        mLayerCanvasMap.clear();

        Scene scene = null;

        if (mSceneType != null) {
            try {
                scene = mSceneType.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (scene == null) {
            layerList.setItems(null);
        } else {
            scene.load();
            layerList.setItems(scene.getLayerList());
        }
        mScene = scene;

        updateTitle();
        updateConfigurationList(-1);
        render();
    }

    private void zoom(int amount) {
        double scale = layerStack.getScaleX();

        if (amount == 0) {
            scale = 1.0f;
        } else if (amount > 0) {
            scale = scale * SCALE_DELTA;
        } else {
            scale = scale * (1 / SCALE_DELTA);
        }

        layerStack.setScaleX(scale);
        layerStack.setScaleY(scale);
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
        final long startTime = System.currentTimeMillis();

        setStatus("Starting rendering");
        final Task<Void> renderTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                mIsRendering.setValue(true);
                for (Layer l : layers) {
                    final Canvas canvas;
                    final Layer layer = l;
                    try {
                        setStatus("rendering:" + layer.getName());
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
                final float elapsed = (float) (System.currentTimeMillis() - startTime) / 1000;
                setStatus(String.format("Rendered in %.4f second(s)", elapsed));
                return null;
            }
        };
        Thread th = new Thread(renderTask);
        th.setDaemon(true);
        th.start();
    }

    private void setBackgroundColor(Color color) {
        final int green = (int) (color.getGreen() * 255);
        final String greenString = Integer.toHexString(green);
        final int red = (int) (color.getRed() * 255);
        final String redString = Integer.toHexString(red);
        final int blue = (int) (color.getBlue() * 255);
        final String blueString = Integer.toHexString(blue);

        String hexColor = "#" + redString + greenString + blueString;

        scrollPane.setStyle("-fx-background: " + hexColor);
    }

    private void setStatus(final String status) {
        Logger.info(status);
        if (Platform.isFxApplicationThread()) {
            statusLabel.textProperty().setValue(status);
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusLabel.textProperty().setValue(status);
            }
        });
    }

    private void updateConfigurationList(int index) {

        if (mOnConfigurationChangedListener == null) {
            mOnConfigurationChangedListener = new Configuration.OnConfigurationChangedListener() {
                @Override
                public void onConfigurationChanged(Configuration config) {
                    render();
                }
            };
        }

        configurationList.getChildren().clear();
        sceneConfiguration.setDisable(false);
        mScene.getConfiguration().removeOnConfigurationChangedListener(mOnConfigurationChangedListener);
        if (index < 0 || index >= mScene.getLayerList().size()) {
            if (mScene != null) {
                Logger.debug("Configuring scene:" + mScene);
                mConfigurationController.configurationProperty().setValue(mScene.getConfiguration());
                mScene.getConfiguration().addOnConfigurationChangedListener(mOnConfigurationChangedListener);
                sceneConfiguration.setDisable(true);
            } else {
                Logger.debug("Clearing configuration");
                mConfigurationController.configurationProperty().setValue(null);
            }
            return;
        }

        final Layer layer = mScene.getLayerList().get(index);
        if (layer == null) {
            return;
        }
        Logger.debug("Configuring layer:" + layer.getName());

        final Configuration config = layer.getConfiguration();
        final Configuration old = mConfigurationController.configurationProperty().getValue();



        if (old != null) {
            old.removeOnConfigurationChangedListener(mOnConfigurationChangedListener);
        }
        config.addOnConfigurationChangedListener(mOnConfigurationChangedListener);
        mConfigurationController.configurationProperty().setValue(config);
    }
}

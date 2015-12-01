package io.firstwave.allium.viewer;

import io.firstwave.allium.Const;
import io.firstwave.allium.api.*;
import io.firstwave.allium.demo.DemoScene;
import io.firstwave.allium.viewer.scene_tree.SceneItem;
import io.firstwave.allium.viewer.scene_tree.TreeTableVisibilityCell;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.pmw.tinylog.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by obartley on 11/30/15.
 */
public class SceneViewerController implements Initializable {

    private static final double SCALE_DELTA = 1.1;

    @FXML
    private Button sceneReload;

    @FXML
    private Button configDiscard;
    @FXML
    private Button configApply;
    @FXML
    private VBox configList;

    @FXML
    private MenuItem menuOpen;
    @FXML
    private MenuItem menuQuit;
    @FXML
    private MenuItem menuZoomOut;
    @FXML
    private MenuItem menuZoomIn;
    @FXML
    private MenuItem menuNoZoom;
    @FXML
    private MenuItem menuReload;
    @FXML
    private MenuItem menuRender;
    @FXML
    private MenuItem menuAbout;

    @FXML
    private Font x3;
    @FXML
    private Color x4;

    @FXML
    private Label statusLeft;

    @FXML
    private StackPane layerStack;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TreeTableView<SceneItem> sceneTree;
    @FXML
    private TreeTableColumn<SceneItem, String> treeName;
    @FXML
    private TreeTableColumn<SceneItem, ObjectProperty<Visibility>> treeVisible;


    private final SimpleBooleanProperty mIsRendering = new SimpleBooleanProperty(false);

    private ConfigurationController mConfigurationController;
    private Stage mStage;
    private Scene mScene;
    private Class<? extends Scene> mSceneType;

    private Configuration.OnConfigurationChangedListener mOnConfigurationChangedListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        treeName.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        treeVisible.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().visibleProperty()));

        treeVisible.setCellFactory(param -> {
            final TreeTableVisibilityCell rv = new TreeTableVisibilityCell<>();
            rv.setAlignment(Pos.CENTER);
            return rv;
        });

        configList.disableProperty().bind(mIsRendering);

        menuOpen.setOnAction(event -> open());

        menuReload.setOnAction(event -> reload());
        menuReload.setDisable(true);

        final EventHandler<ActionEvent> zoom = event -> {
            final MenuItem mnu = (MenuItem) event.getSource();
            zoom((Integer) mnu.getUserData());
        };
        menuZoomIn.setUserData(1);
        menuZoomIn.setOnAction(zoom);
        menuZoomOut.setUserData(-1);
        menuZoomOut.setOnAction(zoom);
        menuNoZoom.setUserData(0);
        menuNoZoom.setOnAction(zoom);

        menuQuit.setOnAction(event -> {
            if (getStage() != null) {
                getStage().close();
            }
        });

        menuRender.setOnAction(event -> {
            if (!menuRender.isDisable()) {
                render();
            }
        });

        menuAbout.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText(Const.APP_NAME + " (" + Const.VERSION + ")");
            alert.setContentText(Const.COPYRIGHT);

            alert.showAndWait();
        });

        mConfigurationController = new ConfigurationController(configList);
        configApply.setOnAction(
                event -> setStatus("Applied " + mConfigurationController.apply() + " change(s)"));
        configDiscard.setOnAction(
                event -> mConfigurationController.cancel());


        mConfigurationController.configurationProperty().addListener(new ChangeListener<Configuration>() {
            @Override
            public void changed(ObservableValue<? extends Configuration> observable, Configuration oldValue, Configuration newValue) {
                configApply.disableProperty().bind(newValue.unchangedProperty());
                configDiscard.disableProperty().bind(newValue.unchangedProperty());
            }
        });

//        openFile(Prefs.getLastPath());
    }



    public void setStage(Stage stage) {
        mStage = stage;
        updateTitle();
    }

    public Stage getStage() {
        return mStage;
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

    private void open() {
        setSceneType(DemoScene.class);
    }

    private void setSceneType(Class<? extends Scene> sceneType) {
        menuReload.setDisable(sceneType == null);
        if (sceneType == null) {
            menuRender.disableProperty().unbind();
            menuRender.setDisable(true);
            menuZoomIn.disableProperty().unbind();
            menuZoomIn.setDisable(true);
            menuZoomOut.disableProperty().unbind();
            menuZoomOut.setDisable(true);
            menuNoZoom.disableProperty().unbind();
            menuNoZoom.setDisable(true);
        } else {
            menuRender.disableProperty().bind(mIsRendering);
            menuZoomIn.disableProperty().bind(mIsRendering);
            menuZoomOut.disableProperty().bind(mIsRendering);
            menuNoZoom.disableProperty().bind(mIsRendering);
        }
        mSceneType = sceneType;
        reload();
    }

    private void reload() {
        for (Node node : layerStack.getChildren()) {
            node.visibleProperty().unbind();
        }

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

        mScene = scene;
        if (mScene != null) {
            scene.load();
        }
        updateSceneTree(mScene);
        updateConfigurationList(mScene);
        updateTitle();
        render();
    }

    private void updateSceneTree(Scene scene) {
        sceneTree.setRoot(null);
        if (scene == null) {
            return;
        }

        final TreeItem<SceneItem> root = new TreeItem<>(new SceneItem(scene.toString()));
        root.setExpanded(true);

        for (Layer l : scene.getLayerList()) {
            root.getChildren().add(new TreeItem<>(new SceneItem(l.getName(), l.visibilityProperty())));
        }

        sceneTree.setRoot(root);
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
        if (mIsRendering.getValue()) {
            Logger.debug("Render in progress -- skipping");
            return;
        }
        layerStack.getChildren().clear();

        if (mScene == null) {
            setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        setBackgroundColor(mScene.getBackgroundColor());

        final Renderer renderer = mScene.createRenderer();
//        layerList.setItems(mScene.getLayerList());
        final List<Layer> layers = new ArrayList<Layer>(mScene.getLayerList());
        final long startTime = System.currentTimeMillis();

        setStatus("Starting rendering");
        final Task<Void> renderTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (Layer layer : layers) {
                    final Canvas canvas;
                    try {
                        setStatus("rendering:" + layer.getName());
                        canvas = renderer.render(layer);
//                        canvas.visibleProperty().bind(layer.visibleProperty());
                        Platform.runLater(() -> layerStack.getChildren().add(canvas));
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
        mIsRendering.setValue(true);
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
            statusLeft.textProperty().setValue(status);
            return;
        }
        Platform.runLater(() -> statusLeft.setText(status));
    }

    private void updateConfigurationList(Configurable configurable) {

        if (mOnConfigurationChangedListener == null) {
            mOnConfigurationChangedListener = config -> render();
        }

        configList.getChildren().clear();


        final Configuration config = configurable.getConfiguration();
        final Configuration old = mConfigurationController.configurationProperty().getValue();

        if (old != null) {
            old.removeOnConfigurationChangedListener(mOnConfigurationChangedListener);
        }
        config.addOnConfigurationChangedListener(mOnConfigurationChangedListener);
        mConfigurationController.configurationProperty().setValue(config);
    }
}

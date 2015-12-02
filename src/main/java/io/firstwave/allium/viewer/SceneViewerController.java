package io.firstwave.allium.viewer;

import io.firstwave.allium.Const;
import io.firstwave.allium.api.Configuration;
import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.demo.DemoScene;
import io.firstwave.allium.utils.FXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.pmw.tinylog.Logger;

import java.net.URL;
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
    private TreeTableView<Layer> sceneTree;
    @FXML
    private TreeTableColumn<Layer, String> nodeName;
    @FXML
    private TreeTableColumn<Layer, Boolean> nodeVisible;


    private ConfigurationController mConfigurationController;
    private Stage mStage;
    private Scene mScene;
    private Class<? extends Scene> mSceneType;

    private Configuration.OnConfigurationChangedListener mOnConfigurationChangedListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        nodeName.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        nodeVisible.setCellValueFactory(param -> param.getValue().getValue().visibleProperty());
        nodeVisible.setCellFactory(param -> {
            final CheckBoxTreeTableCell<Layer, Boolean> rv = new CheckBoxTreeTableCell<>();
            rv.setAlignment(Pos.CENTER);
            return rv;
        });
        sceneTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateConfigurationList(newValue.getValue().getConfiguration());
        });

//        configList.disableProperty().bind(mIsRendering);

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


        mConfigurationController.configurationProperty().addListener((observable, oldValue, newValue) -> {
            configApply.disableProperty().bind(newValue.unchangedProperty());
            configDiscard.disableProperty().bind(newValue.unchangedProperty());
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
//            menuRender.disableProperty().bind(mIsRendering);
//            menuZoomIn.disableProperty().bind(mIsRendering);
//            menuZoomOut.disableProperty().bind(mIsRendering);
//            menuNoZoom.disableProperty().bind(mIsRendering);
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
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        mScene = scene;
        if (mScene != null) {
            scene.load();
            setBackgroundColor(mScene.getBackgroundColor());
            mScene.backgroundColorProperty().addListener((observable, oldValue, newValue) -> {
                setBackgroundColor(newValue);
            });

        }
        updateSceneTree(mScene);
        updateTitle();
        render();
    }

    private void updateSceneTree(Scene scene) {
        sceneTree.setRoot(null);
        if (scene == null || scene.getRoot() == null) {
            return;
        }

        final TreeItem<Layer> root = new TreeItem<>(scene.getRoot());
        Logger.debug("adding root node:" + scene.getRoot().getName());
        sceneTree.setRoot(root);
        root.setExpanded(true);
        addSceneNode(root);

    }

    private void addSceneNode(TreeItem<Layer> root) {
        for (Layer node : root.getValue().getChildNodes()) {
            final TreeItem<Layer> treeNode = new TreeItem<>(node);
            treeNode.setExpanded(true);
            root.getChildren().add(treeNode);
            Logger.debug("adding node:" + node.getName());
            if (node.getChildCount() > 0) {
                addSceneNode(treeNode);
            }
        }
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
        if (mScene == null) {
            Logger.debug("Skipping render");
            return;
        }

        layerStack.getChildren().clear();

        final RenderContext ctx = new RenderContext(mScene.getWidth(), mScene.getHeight(),
                layer -> {
                    // called on the render thread, we need to push it back to the main thread
                    if (layer != null) {
                        final Canvas canvas = layer.getCanvas();
                        if (canvas != null) {
                            Logger.debug("publishing:" + layer);
                            FXUtils.runOnMainThread(() -> SceneViewerController.this.publish(layer, canvas));
                        }
                    }
                },
                Logger::debug);

        mScene.render(ctx);
    }

    private void publish(Layer layer, Canvas canvas) {
        Logger.debug("published:" + layer);
        layerStack.getChildren().add(canvas);
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
        FXUtils.runOnMainThread(() -> statusLeft.setText(status));
    }

    private void updateConfigurationList(Configuration configuration) {

        if (mOnConfigurationChangedListener == null) {
            mOnConfigurationChangedListener = config -> render();
        }

        configList.getChildren().clear();


        final Configuration old = mConfigurationController.configurationProperty().getValue();

        if (old != null) {
            old.removeOnConfigurationChangedListener(mOnConfigurationChangedListener);
        }
        if (configuration != null) {
            configuration.addOnConfigurationChangedListener(mOnConfigurationChangedListener);
        }
        mConfigurationController.configurationProperty().setValue(configuration);
    }
}

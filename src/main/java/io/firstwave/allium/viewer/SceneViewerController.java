package io.firstwave.allium.viewer;

import io.firstwave.allium.Const;
import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.demo.DemoScene;
import io.firstwave.allium.utils.FXUtils;
import io.firstwave.allium.viewer.ui.LayerNameTreeTableCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.ImageView;
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
    private Label statusMessage;

    @FXML
    private StackPane layerStack;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TreeTableView<Layer> sceneTree;
    @FXML
    private TreeTableColumn<Layer, Layer> nodeName;
    @FXML
    private TreeTableColumn<Layer, Boolean> nodeVisible;
    @FXML
    private TreeTableColumn<Layer, String> nodeMessage;

    @FXML
    private TextArea textLog;
    @FXML
    private ProgressBar progress;


    private OptionsController mOptionsController;
    private Stage mStage;
    private Scene mScene;
    private Class<? extends Scene> mSceneType;

    private final SimpleBooleanProperty mLocked = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSceneTree();
        initMenus();
        initOptions();
    }

    private void initSceneTree() {
        nodeName.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));

        nodeName.setCellFactory(LayerNameTreeTableCell.getFactory());
        nodeVisible.setCellValueFactory(param -> param.getValue().getValue().visibleProperty());
        nodeVisible.setCellFactory(param -> {
            final CheckBoxTreeTableCell<Layer, Boolean> rv =
                    new CheckBoxTreeTableCell<>();
            rv.setAlignment(Pos.CENTER);
            return rv;
        });

        nodeMessage.setCellValueFactory(param -> param.getValue().getValue().messageProperty());
        nodeMessage.setCellFactory(param -> {
            final TextFieldTreeTableCell<Layer, String> rv = new TextFieldTreeTableCell<Layer, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    setWrapText(false);
                    setText(null);
                    if (item == null) {
                        Tooltip.install(this, null);
                        return;
                    }
                    String[] split = item.split(System.getProperty("line.separator"));
                    if (split.length > 0) {
                        setText(split[0]);
                    } else {
                        setText(item);
                    }
                    Tooltip tt = new Tooltip(item);
                    tt.setWrapText(true);
                    Tooltip.install(this, tt);
                }
            };
            rv.setOpacity(0.5);

            return rv;
        });

        sceneTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateOptionsList(newValue.getValue().getOptions());
        });
    }

    private void initMenus() {
        menuOpen.setOnAction(event -> open());
        menuReload.setOnAction(event -> reload());
        menuRender.disableProperty().bind(mLocked);

//        menuZoomIn.disableProperty().bind(mLocked);
//        menuZoomOut.disableProperty().bind(mLocked);
//        menuNoZoom.disableProperty().bind(mLocked);

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
            ImageView icon = new ImageView("/images/icon_512.png");
            icon.setFitHeight(64);
            icon.setFitWidth(64);
            alert.setGraphic(icon);
            alert.setTitle("About");
            alert.setHeaderText(Const.APP_NAME + " (" + Const.VERSION + ")");
            alert.setContentText(Const.COPYRIGHT);

            alert.showAndWait();
        });
    }

    private void initOptions() {
        configList.disableProperty().bind(mLocked);

        sceneReload.setOnAction(event -> reload());


        OptionsController.registerDefaultBinders();
        mOptionsController = new OptionsController(configList);


        configApply.disableProperty().bind(mLocked);
        configApply.setOnAction(event1 -> {
                    render();
                }
        );
        configDiscard.disableProperty().bind(mLocked);
        configDiscard.setOnAction(event -> mOptionsController.reset());
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
        // TODO: implement file picker stuff
        setSceneType(DemoScene.class);
    }

    private void setSceneType(Class<? extends Scene> sceneType) {
        menuReload.setDisable(sceneType == null);
        mSceneType = sceneType;
        reload();
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

    private void reload() {
        // TODO: we should probably handle reload during a long-runnin render a bit better
        // i think reloading during a render will not prevent RenderContext callbacks from being triggered
        // we might want to hang on to a render context ref and add a cancel() flag

        Logger.trace("reload");
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
            mScene.renderContextProperty().addListener((observable, oldValue, newValue) -> {
                Logger.debug("Rendering lock:" + newValue);
                mLocked.set(newValue != null);
                progress.setProgress(0);
            });
        }
        updateOptionsList(null);
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

        sceneTree.setRoot(root);

        if (sceneTree.getSelectionModel().selectedItemProperty().getValue() != null) {
            final Layer sel = sceneTree.getSelectionModel().selectedItemProperty().getValue().getValue();
            if (sel != null) {
                updateOptionsList(sel.getOptions());
            }
        }

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

    private void render() {
        if (mScene == null || mLocked.getValue()) {
            Logger.debug("Skipping render");
            return;
        }
        mOptionsController.apply();
        layerStack.getChildren().clear();

        final RenderContext context = new RenderContext(mScene.getWidth(), mScene.getHeight(),
                (ctx, layer) -> {
                    // called on the render thread, we need to push it back to the main thread
                    if (layer != null) {
                        final Canvas canvas = layer.getCanvas();
                        if (canvas != null) {
                            Logger.trace("publishing:" + layer);
                            FXUtils.runOnMainThread(() -> {
                                SceneViewerController.this.publish(layer, canvas);
                                final double total = ctx.getLayerCount();
                                final double count = ctx.getPublishCount();
                                progress.setProgress(count / total);
                            });
                        }
                    }
                },
                (ctx, source, message) -> setStatus("[" + source + "] " + message),
                (ctx, source, tr) -> setStatus("[" + source + "] " + tr.toString()));
        progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        mScene.render(context);

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
        FXUtils.runOnMainThread(() -> {
            statusMessage.setText(status);
            textLog.appendText(status + "\n");
        });
    }

    private void updateOptionsList(Options options) {
        mOptionsController.setOptions(options);
    }
}

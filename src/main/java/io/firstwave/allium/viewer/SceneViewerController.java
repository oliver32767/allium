package io.firstwave.allium.viewer;

import io.firstwave.allium.Const;
import io.firstwave.allium.Prefs;
import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.demo.GameScene;
import io.firstwave.allium.utils.FXUtils;
import io.firstwave.allium.viewer.ui.LayerNameTreeTableCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
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
import java.util.*;

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
    private CheckMenuItem menuTheme;

    @FXML
    private Font x3;
    @FXML
    private Color x4;

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
    private TreeTableColumn<Layer, Layer> nodeStatus;

    @FXML
    private TextArea textLog;
    @FXML
    private ProgressBar progress;

    @FXML
    private TextField textSeed;


    private OptionsController mOptionsController;
    private Stage mStage;
    private Scene mScene;
    private Class<? extends Scene> mSceneType;

    private List<Layer> mLayerOrder = new ArrayList<>();

    private double mScrollH, mScrollV;

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

        nodeStatus.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));
        nodeStatus.setCellFactory(param -> {
            final TreeTableCell<Layer, Layer> rv = new TextFieldTreeTableCell<Layer, Layer>() {
                @Override
                public void updateItem(Layer item, boolean empty) {
                    if (item == null || empty) {
                        setText(null);
                        return;
                    }
                    if (item.getOptions() == null || item.getOptions().getKeys().size() == 0) {
                        setText(null);
                    } else {
                        setText("⚙");
                    }
                }
            };
            rv.setAlignment(Pos.CENTER);
            return rv;
        });
        sceneTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                updateOptionsList(null);
            } else {
                updateOptionsList(newValue.getValue().getOptions());
            }
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

        menuTheme.setOnAction(event -> setDarkTheme(menuTheme.isSelected()));

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

    public void setDarkTheme(boolean dark) {
        if (mStage == null) {
            return;
        }
        if (dark) {
            mStage.getScene().getStylesheets().add("/styles/dark.css");
        } else {
            mStage.getScene().getStylesheets().remove("/styles/dark.css");
        }

        if (dark != menuTheme.isSelected()) {
            menuTheme.setSelected(dark);
        }
        Prefs.setDarkTheme(dark);
    }

    private void open() {
        // TODO: implement file picker stuff
        setSceneType(GameScene.class);
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
        bindSceneNode(root);

        sceneTree.setRoot(root);

        if (sceneTree.getSelectionModel().selectedItemProperty().getValue() != null) {
            final Layer sel = sceneTree.getSelectionModel().selectedItemProperty().getValue().getValue();
            if (sel != null) {
                updateOptionsList(sel.getOptions());
            }
        }

        mLayerOrder.clear();
        mLayerOrder.add(scene.getRoot());

        updateSceneNode(root);

    }

    private void updateSceneNode(final TreeItem<Layer> root) {

        // this handles automatically updating the scene tree
        // when changes are observed in a layer's child list
        final Layer layer = root.getValue();
        layer.getChildNodes().addListener(new ListChangeListener<Layer>() {
            @Override
            public void onChanged(Change<? extends Layer> c) {
                Logger.debug("scene tree changed");
                layer.getChildNodes().removeListener(this);
                root.getChildren().clear();
                updateSceneNode(root);
                sceneTree.getSelectionModel().clearSelection();
            }
        });

        for (Layer node : root.getValue().getChildNodes()) {
            final TreeItem<Layer> treeNode = new TreeItem<>(node);
            bindSceneNode(treeNode);
            root.getChildren().add(treeNode);
            Logger.debug("adding node:" + node.getName());
            mLayerOrder.add(node);
            updateSceneNode(treeNode);
        }
    }

    private void bindSceneNode(TreeItem<Layer> node) {
        node.setExpanded(node.getValue().isExpanded());
        node.expandedProperty().bindBidirectional(node.getValue().expandedProperty());
    }

    private void render() {
        if (mScene == null || mLocked.getValue()) {
            Logger.debug("Skipping render");
            return;
        }

        long seed;
        final String seedStr = textSeed.getText();
        if (seedStr == null || "".equals(seedStr)) {
            seed = new Random(System.currentTimeMillis()).nextLong();
        } else  {
            try {
                seed = Long.parseLong(seedStr);
            } catch (NumberFormatException nfe) {
                seed = Objects.hashCode(seedStr);
            }
        }

        mOptionsController.apply();

        mScrollH = scrollPane.getHvalue();
        mScrollV = scrollPane.getVvalue();



        final RenderContext context = new RenderContext(seed,
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
        layerStack.getChildren().clear();
        mScene.render(context);
    }

    private void publish(Layer layer, Canvas canvas) {
        final int index = mLayerOrder.indexOf(layer);
        if (index < 0) {
            Logger.warn("Unable to determine z-index of layer:" + layer);
            return;
        }

        canvas.setUserData(index);

        boolean added = false;
        for (int i = 0; i < layerStack.getChildren().size(); i++) {
            final int idx = (int) layerStack.getChildren().get(i).getUserData();
            if (idx > index) {
                layerStack.getChildren().add(i, canvas);
                added = true;
                break;
            }
        }
        if (!added) {
            layerStack.getChildren().add(canvas);
        }

        if (mScrollV >= 0 || mScrollH >= 0) {
            scrollPane.setHvalue(mScrollH);
            scrollPane.setVvalue(mScrollV);
            mScrollV = -1;
            mScrollH = -1;
        }
    }

    private void setBackgroundColor(Color color) {
        final int green = (int) (color.getGreen() * 255);
        final String greenString = Integer.toHexString(green);
        final int red = (int) (color.getRed() * 255);
        final String redString = Integer.toHexString(red);
        final int blue = (int) (color.getBlue() * 255);
        final String blueString = Integer.toHexString(blue);

        final String hexColor = "#" + redString + greenString + blueString;

        scrollPane.setStyle("-fx-background: " + hexColor);
    }

    private void setStatus(final String status) {
        Logger.info(status);
        FXUtils.runOnMainThread(() -> {
            textLog.appendText(status + "\n");
        });
    }

    private void updateOptionsList(Options options) {
        mOptionsController.setOptions(options);
    }
}

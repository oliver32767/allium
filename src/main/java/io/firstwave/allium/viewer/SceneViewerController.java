package io.firstwave.allium.viewer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Created by obartley on 11/30/15.
 */
public class SceneViewerController {
    @FXML
    private Button configDiscard;

    @FXML
    private MenuItem menuZoomOut;

    @FXML
    private MenuItem menuOpen;

    @FXML
    private VBox configList;

    @FXML
    private Button sceneReload;

    @FXML
    private Button configApply;

    @FXML
    private MenuItem menuQuit;

    @FXML
    private MenuItem menuNoZoom;

    @FXML
    private MenuItem menuZoomIn;

    @FXML
    private MenuItem menuReload;

    @FXML
    private MenuItem menuRender;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    @FXML
    private Label statusLeft;

    @FXML
    private MenuItem menuAbout;
}

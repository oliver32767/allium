package io.firstwave.allium;

import io.firstwave.allium.viewer.SceneViewerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class Allium extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scene_viewer.fxml"));
        final ResourceBundle rb = getResourceBundle();
        loader.setResources(rb);

        final Parent root = loader.load();
        final SceneViewerController controller = loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setOnCloseRequest(event -> Platform.exit());
        primaryStage.getIcons().addAll(new Image("/images/icon_512.png"));


        configureStageForOSX(primaryStage);

        primaryStage.show();
    }

    private void configureStageForOSX(Stage stage) {
        try {
            java.awt.Image image = ImageIO.read(getClass().getResourceAsStream("/images/icon_512.png"));
            com.apple.eawt.Application.getApplication().setDockIconImage(image);
        } catch (Exception e) {
            // Won't work on Windows or Linux.
        }
    }

    private ResourceBundle getResourceBundle() {
        return new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return null;
            }

            @Override
            public Enumeration<String> getKeys() {
                return null;
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}

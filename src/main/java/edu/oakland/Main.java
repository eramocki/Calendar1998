package edu.oakland;

import edu.oakland.GUI.LoginGUI;
import edu.oakland.GUI.MainGUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static File DATA_DIR = new File(System.getProperty("user.home") + "/Calendar1998/");

    public static void main(String[] args) {
        if (!DATA_DIR.exists()) {
            if (!DATA_DIR.mkdirs()) {
                logger.warning("Couldn't create save directory!");
            }
        }
        Account.loadAccounts();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resourceFXML = getClass().getClassLoader().getResource("LoginGUI.fxml");
        URL resourceCSS = getClass().getClassLoader().getResource("mystyle.css");
        if (resourceFXML == null || resourceCSS == null) {
            System.out.println("Missing resource detected, ABORT!");
            System.exit(-1);
        }
        Parent root = FXMLLoader.load(resourceFXML);
        Scene scene = new Scene(root, 400, 400);
        String css = resourceCSS.toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cadmium Calendar");
        primaryStage.show();
    }
}

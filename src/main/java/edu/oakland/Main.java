package edu.oakland;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static final File DATA_DIR = new File(System.getProperty("user.home") + "/Calendar1998/");

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
        java.net.URL resource = getClass().getClassLoader().getResource("LoginGUI.fxml");
        if (resource == null) {
            resource = getClass().getResource("LoginGUI.fxml");
        }
        Parent root = FXMLLoader.load(resource);
        primaryStage.setTitle("Cadmium Calendar");
        primaryStage.setScene(new Scene(root, 600, 300));
        primaryStage.show();
    }
}

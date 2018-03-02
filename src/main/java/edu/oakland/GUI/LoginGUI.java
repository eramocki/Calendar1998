package edu.oakland.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginGUI {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;
    @FXML
    private Button createAccountButton;


    @FXML
    public void initialize() {

        usernameField.setText("testing");
        
    }

    @FXML
    private void tryLogin(ActionEvent event) {
        if (checkPassword(usernameField.getText(), passwordField.getText())) {
            try {
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainGUI.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Calendar 1998");
                stage.setScene(new Scene(root, 650, 650));
                stage.show();

                ((Node) (event.getSource())).getScene().getWindow().hide();

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("This will not do.");
                alert.setHeaderText("Woah! There was a problem logging in!");
                alert.setContentText(e.getMessage());

                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("Username / password incorrect");

            alert.showAndWait();
        }
    }

    private boolean checkPassword(String username, String password) {
        return username.equals("changeme") && password.equals("123");
    }

}

package edu.oakland.GUI;

import edu.oakland.Account;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginGUI {

    private transient static final Logger logger = Logger.getLogger(LoginGUI.class.getName());

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

//        Account.createAccount("changeme", "123"); //Dummy account
    }

    @FXML
    private void tryLogin(ActionEvent event) {
        if (Account.accountExists(usernameField.getText())) {
            if (Account.login(usernameField.getText(), passwordField.getText())) {
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
                    logger.log(Level.SEVERE, "Error logging in", e);

                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("This will not do.");
                alert.setHeaderText("Try again, friend.");
                alert.setContentText("Incorrect username / password combination");

                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Account not found.");
            alert.setContentText("No such account found. You can create one using the button below.");

            alert.showAndWait();
        }
    }

    @FXML
    private void tryCreateAccount() {
        if (Account.accountExists(usernameField.getText())) {
            errorAlert("Creation Failed", "Account Creation Failed",
                    "Account with that name already exists");
        } else if (passwordField.getText().equals("")) {
             errorAlert("Creation Failed", "Account Creation Failed", "Password field cannot be " +
                     "empty!");
        } else { //Attempt to create account
            if (Account.createAccount(usernameField.getText(), passwordField.getText())) {
                alert("Account Created", "Account Successfully Created", "You can now login with" +
                        " the provided information", Alert.AlertType.CONFIRMATION);
            } else {
                errorAlert("Creation Failed", "Account Creation Failed", "Account creation " +
                        "failed for an unknown reason.");
            }
        }
    }

    //To clean up the code a bit
    private void errorAlert(String title, String header, String content) {
        alert(title, header, content, Alert.AlertType.ERROR);
    }

    private void alert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}

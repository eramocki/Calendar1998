package edu.oakland.GUI;

import edu.oakland.Account;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginGUI {

    private static final Logger logger = Logger.getLogger(LoginGUI.class.getName());

    @FXML
    private TextField userField, userFieldReset, nameField, sq1, sq2, sq3, sq1Reset, sq2Reset, sq3Reset;

    @FXML
    private PasswordField passwordField, passwordField_confirm, passwordFieldReset, passwordFieldReset_confirm;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {

        //Dummy Account
        Account.createAccount("y", "y", "Test Account", new String[]{"1", "2", "3"});
    }

    @FXML
    private void importData(ActionEvent event) {
        //TODO
    }

    @FXML
    private void exportData(ActionEvent event) {
        //TODO
    }

    @FXML
    private void saveData(ActionEvent event) {
        GUIHelper.errorAlert("This will not do.",
                "Woah! There was a problem saving!",
                "How can you save if you aren't logged in?!");
    }

    @FXML
    private void logoutApp(ActionEvent event) {
        GUIHelper.errorAlert("This will not do.",
                "Woah! There was a problem logging out!",
                "How can you logout if you aren't logged in?!");
    }

    @FXML
    private void exitApp(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void aboutApp(ActionEvent event) {
        GUIHelper.alert("Cadmium Calendar",
                "Copyright 2018",
                "Created by:\nIsida Ndreu\nJustin Kur\nSean Ramocki\nEric Ramocki\nJosh Baird\nMichael Koempel",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void gotoAddEventPageTab(ActionEvent event) {
        GUIHelper.errorAlert("This will not do.",
                "Woah! There was a problem adding a new event!",
                "How can you add events if you aren't logged in?!");
    }

    @FXML
    private void openSettingsGUI(ActionEvent event) {
        GUIHelper.errorAlert("This will not do.",
                "Woah! There was a problem going to the settings page",
                "How can you edit settings if you aren't logged in?!");
    }

    @FXML
    private void tryLogin(ActionEvent event) {
        if (Account.accountExists(userField.getText())) {
            if (Account.checkCredentials(userField.getText(), passwordField.getText())) {
                try {
                    //Get resource for FXML file
                    java.net.URL resource = getClass().getClassLoader().getResource("MainGUI.fxml");
                    if (resource == null) {
                        resource = getClass().getResource("MainGUI.fxml");
                    }
                    FXMLLoader fxmlLoader = new FXMLLoader(resource);
                    Parent root = fxmlLoader.load();

                    //Get a reference to the instance of MainGUI
                    MainGUI mainGUI = fxmlLoader.getController();
                    mainGUI.setCurrentAccount(Account.getAccount(userField.getText()));
                    mainGUI.postInit();

                    //Create the window
                    Stage stage = new Stage();
                    stage.setTitle(Account.getName(userField.getText()) + "'s Calendar");
                    stage.setScene(new Scene(root));
                    stage.show();

                    //Hide login window
                    ((Node) (event.getSource())).getScene().getWindow().hide();

                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error logging in", e);
                    GUIHelper.errorAlert("This will not do.", "Woah! There was a problem logging in!", e.getMessage());
                }
            } else {
                GUIHelper.errorAlert("This will not do.",
                        "Try again, friend.",
                        "Incorrect username / password combination");
            }
        } else {
            GUIHelper.errorAlert("This will not do.",
                    "Account not found.",
                    "No such account found. You can create one using the button below.");
        }
    }

    @FXML
    public void openCreateAccountGUI(ActionEvent event) {
        Stage stage;
        try {
            stage = new Stage();
            java.net.URL resource = getClass().getClassLoader().getResource("CreateAccountGUI.fxml");
            if (resource == null) {
                resource = getClass().getResource("CreateAccountGUI.fxml");
            }
            Parent root2 = FXMLLoader.load(resource);
            stage.setScene(new Scene(root2));
            stage.setTitle("Create Account");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(loginButton.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void openReset(ActionEvent event) {
        Stage stage;
        try {
            stage = new Stage();
            java.net.URL resource = getClass().getClassLoader().getResource("ResetAccountGUI.fxml");
            if (resource == null) {
                resource = getClass().getResource("ResetAccountGUI.fxml");
            }
            Parent root3 = FXMLLoader.load(resource);
            stage.setScene(new Scene(root3));
            stage.setTitle("Reset Account");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(loginButton.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void tryResetAccount(ActionEvent event) {
        if (Account.accountExists(userFieldReset.getText())) {
            if (!passwordFieldReset_confirm.getText().equals(passwordFieldReset.getText())) {
                GUIHelper.errorAlert("Reset Failed",
                        "Account Reset Failed",
                        "Your confirmation password did not match!");
            } else if (passwordFieldReset.getText().length() < 8 || passwordFieldReset.getText().equals("")) {
                GUIHelper.errorAlert("Reset Failed",
                        "Account Reset Failed",
                        "Password must be at least 8 characters long");
            } else if (!passwordFieldReset.getText().matches(".*\\d+.*")) {
                GUIHelper.errorAlert("Reset Failed",
                        "Account Reset Failed",
                        "Password must contain at least one number");
            } else {
                Account temp = Account.getAccount(userFieldReset.getText());
                if (temp.resetPassword(passwordFieldReset.getText(),
                        new String[]{sq1Reset.getText(), sq2Reset.getText(), sq3Reset.getText()})) {
                    GUIHelper.alert("Account Reset",
                            "Account Successfully Reset",
                            "You can now login with the updated password",
                            Alert.AlertType.CONFIRMATION);
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                } else {
                    GUIHelper.errorAlert("Reset Failed",
                            "Account Reset Failed",
                            "One or more security questions are incorrect");
                }
            }
        } else {
            GUIHelper.errorAlert("Reset Failed", "Account Reset Failed", "Account doesn't exist");
        }
    }


    @FXML
    private void tryCreateAccount(ActionEvent event) {
        if (Account.accountExists(userField.getText())) {
            GUIHelper.errorAlert("Creation Failed", "Account Creation Failed", "Account with that name already exists");
        } else if (passwordField.getText().equals("") || passwordField_confirm.getText().equals("")) {
            GUIHelper.errorAlert("Creation Failed", "Account Creation Failed", "Password field cannot be empty!");
        } else if (sq1.getText().equals("") || sq2.getText().equals("") || sq3.getText().equals("")) {
            GUIHelper.errorAlert("Creation Failed",
                    "Account Creation Failed",
                    "Security question fields cannot be empty!");
        } else if (!passwordField_confirm.getText().equals(passwordField.getText())) {
            GUIHelper.errorAlert("Creation Failed",
                    "Account Creation Failed",
                    "Your confirmation password did not match!");
        } else if (nameField.getText().equals("")) {
            GUIHelper.errorAlert("Creation Failed", "Account Creation Failed", "Your name cannot be blank!");
        } else if (passwordField.getText().length() < 8) {
            GUIHelper.errorAlert("Creation Failed",
                    "Account Creation Failed",
                    "Password must be at least 8 characters long");
        } else if (!passwordField.getText().matches(".*\\d+.*")) {
            GUIHelper.errorAlert("Creation Failed",
                    "Account Creation Failed",
                    "Password must contain at least one number");
        } else {
            //Trys to create account, passes all fields to be stored in map
            if (Account.createAccount(userField.getText(),
                    passwordField.getText(),
                    nameField.getText(),
                    new String[]{sq1.getText(), sq2.getText(), sq3.getText()})) {
                GUIHelper.alert("Account Created",
                        "Account Successfully Created",
                        "You can now login with the provided information",
                        Alert.AlertType.CONFIRMATION);
                ((Node) (event.getSource())).getScene().getWindow().hide();
            } else {
                GUIHelper.errorAlert("Creation Failed",
                        "Account Creation Failed",
                        "Account creation failed for an unknown reason.");
            }
        }
    }
}

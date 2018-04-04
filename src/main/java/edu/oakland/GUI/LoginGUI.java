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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginGUI {

    private static final Logger logger = Logger.getLogger(LoginGUI.class.getName());

    @FXML
    private TextField userField, userFieldReset, nameField, sq1, sq2, sq3, sq1Reset, sq2Reset, sq3Reset;

    @FXML
    private PasswordField passwordField, passwordField_confirm, passwordFieldReset, passwordFieldReset_confirm;

    @FXML
    private Hyperlink pageLink;

    @FXML
    public void initialize() {

        //Dummy Account
        //TODO Delete
        Account.createAccount("y", "y", "Test Account", new String[]{"1", "2", "3"});
    }

    /**
     * Allows the user to import data from the menubar before logging in.
     */
    @FXML
    private void importData() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import data");
        chooser.setInitialDirectory(Account.getAccountFile().getParentFile());
        File importFile = chooser.showOpenDialog(new Stage());

        if (importFile == null) return;

        if (Account.loadAccounts(importFile)){
            GUIHelper.alert("Load me", "Data were loaded", "Loaded data successfully!", Alert.AlertType.INFORMATION);
        } else {
            GUIHelper.errorAlert("This will not do.", "Couldn't load data!", "There was an error loading that file!");
        }
    }

    @FXML
    private void exitApp() {
        Platform.exit();
    }

    @FXML
    private void aboutApp() {
        GUIHelper.alert("Cadmium Calendar",
                "Copyright 2018",
                "Created by:\nIsida Ndreu\nJustin Kur\nSean Ramocki\nEric Ramocki\nJosh Baird\nMichael Koempel",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void tryLogin(ActionEvent event) {
        if(userField.getText().equals("") || passwordField.getText().equals("")) {
            GUIHelper.errorAlert("Input Error", "Login Failed", "The login fields cannot be empty!");
        } else if (Account.accountExists(userField.getText())) {
            if (Account.checkCredentials(userField.getText(), passwordField.getText())) {
                try {
                    Stage stage = new Stage();
                    URL resourceFXML = getClass().getClassLoader().getResource("MainGUI.fxml");
                    URL resourceCSS = getClass().getClassLoader().getResource("mystyle.css");
                    if (resourceFXML == null || resourceCSS == null) {
                        System.out.println("Missing resource detected, ABORT!");
                        System.exit(-1);
                    }
                    FXMLLoader fxmlLoader = new FXMLLoader(resourceFXML);
                    Parent root = fxmlLoader.load();

                    //Get a reference to the instance of MainGUI
                    MainGUI mainGUI = fxmlLoader.getController();
                    mainGUI.setCurrentAccount(Account.getAccount(userField.getText()));
                    mainGUI.postInit();

                    //Create the window
                    Scene scene = new Scene(root, 880, 700);
                    String css = resourceCSS.toExternalForm();
                    scene.getStylesheets().add(css);
                    stage.setScene(scene);
                    stage.setTitle(Account.getName(userField.getText()) + "'s Calendar");
                    stage.show();

                    //Hide login window
                    ((Node) (event.getSource())).getScene().getWindow().hide();

                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error logging in", e);
                    GUIHelper.errorAlert("System Error", "Login Failed", e.getMessage());
                }
            } else {
                GUIHelper.errorAlert("Input Error",
                        "Login Failed",
                        "Incorrect username / password combination");
            }
        } else {
            GUIHelper.errorAlert("Input Error",
                    "Account not found.",
                    "No such account found. You can create one using the button below.");
        }
    }

    @FXML
    public void openCreateAccountGUI() {
        Stage stage;
        try {
            stage = new Stage();
            URL resourceFXML = getClass().getClassLoader().getResource("CreateAccountGUI.fxml");
            URL resourceCSS = getClass().getClassLoader().getResource("mystyle.css");
            if (resourceFXML == null || resourceCSS == null) {
                System.out.println("Missing resource detected, ABORT!");
                System.exit(-1);
            }
            Parent root = FXMLLoader.load(resourceFXML);
            Scene scene = new Scene(root, 400, 600);
            String css = resourceCSS.toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setTitle("Create Account");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(pageLink.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openReset() {
        Stage stage;
        try {
            stage = new Stage();
            URL resourceFXML = getClass().getClassLoader().getResource("ResetAccountGUI.fxml");
            URL resourceCSS = getClass().getClassLoader().getResource("mystyle.css");
            if (resourceFXML == null || resourceCSS == null) {
                System.out.println("Missing resource detected, ABORT!");
                System.exit(-1);
            }
            Parent root = FXMLLoader.load(resourceFXML);
            Scene scene = new Scene(root, 400, 500);
            String css = resourceCSS.toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setTitle("Reset Account");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(pageLink.getScene().getWindow());
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

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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginGUI {

    private transient static final Logger logger = Logger.getLogger(LoginGUI.class.getName());

    @FXML
    private TextField userField, userFieldReset, nameField, sq1, sq2, sq3, sq1Reset, sq2Reset, sq3Reset;
    @FXML
    private PasswordField passwordField, passwordField_confirm, passwordFieldReset, passwordFieldReset_confirm;
    @FXML
    private Button loginButton, createAccountButton;

    /**
     *
     */
    @FXML
    public void initialize() {

        //Dummy Account
        Account.createAccount("y", "y", "Test Account", new String[] {"1", "2", "3"});
    }

    /**
     *
     * @param event
     */
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

                    //Create the window
                    Stage stage = new Stage();
                    stage.setTitle(Account.getName(userField.getText()) + "'s Calendar");
                    stage.setScene(new Scene(root, 650, 650));
                    stage.show();

                    //Hide login window
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

    /**
     *
     * @param event
     */
    @FXML
    public void openCreate(ActionEvent event) {
        Stage stage;
            try {
                stage = new Stage();
                java.net.URL resource = getClass().getClassLoader().getResource("CreateGUI.fxml");
                if (resource == null) {
                    resource = getClass().getResource("CreateGUI.fxml");
                }
                Parent root2 = FXMLLoader.load(resource);
                stage.setScene(new Scene(root2));
                stage.setTitle("Create Account");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(createAccountButton.getScene().getWindow());
                stage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     *
     * @param event
     */
    @FXML
    public void openReset(ActionEvent event) {
        Stage stage;
        try {
            stage = new Stage();
            java.net.URL resource = getClass().getClassLoader().getResource("ResetGUI.fxml");
            if (resource == null) {
                resource = getClass().getResource("ResetGUI.fxml");
            }
            Parent root3 = FXMLLoader.load(resource);
            stage.setScene(new Scene(root3));
            stage.setTitle("Reset Account");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(createAccountButton.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void tryResetAccount(ActionEvent event){
        if (Account.accountExists(userFieldReset.getText())) {
            if (!passwordFieldReset_confirm.getText().equals(passwordFieldReset.getText())) {
                    errorAlert("Reset Failed", "Account Reset Failed", "Your confirmation " +
                            "password did not match!");
            } else if (passwordFieldReset.getText().length() < 8 || passwordFieldReset.getText().equals("")) {
                    errorAlert("Reset Failed", "Account Reset Failed", "Password must be at least " +
                            "8 characters long");
            } else if (!passwordFieldReset.getText().matches(".*\\d+.*")) {
                errorAlert("Reset Failed", "Account Reset Failed", "Password must contain at " + "least one number");
            } else {
                Account temp = Account.getAccount(userFieldReset.getText());
                if (temp.resetPassword(passwordFieldReset.getText(), new String[] {sq1Reset.getText(), sq2Reset.getText(), sq3Reset.getText()})){
                    alert("Account Reset", "Account Successfully Reset", "You can now login with" +
                            " the updated password", Alert.AlertType.CONFIRMATION);
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                } else {
                    errorAlert("Reset Failed", "Account Reset Failed", "One or more security questions are incorrect");
                }
            }
        } else {
            errorAlert("Reset Failed", "Account Reset Failed", "Account doesn't exist");
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void tryCreateAccount(ActionEvent event) {
        if (Account.accountExists(userField.getText())) {
            errorAlert("Creation Failed", "Account Creation Failed",
                    "Account with that name already exists");
        } else if (passwordField.getText().equals("") || passwordField_confirm.getText().equals("")) {
             errorAlert("Creation Failed", "Account Creation Failed", "Password field cannot be " +
                     "empty!");
        } else if (sq1.getText().equals("") || sq2.getText().equals("") || sq3.getText().equals("")) {
            errorAlert("Creation Failed", "Account Creation Failed", "Security question fields " +
                    "cannot be empty!");
        } else if (!passwordField_confirm.getText().equals(passwordField.getText())) {
            errorAlert("Creation Failed", "Account Creation Failed", "Your confirmation " +
                    "password did not match!");
        } else if (nameField.getText().equals("")) {
            errorAlert("Creation Failed", "Account Creation Failed", "Your name cannot be blank!");
        } else if (passwordField.getText().length() < 8) {
            errorAlert("Creation Failed", "Account Creation Failed", "Password must be at least " +
                    "8 characters long");
        } else if (!passwordField.getText().matches(".*\\d+.*")){
            errorAlert("Creation Failed", "Account Creation Failed", "Password must contain at " +
                    "least one number");
        } else {
            //Trys to create account, passes all fields to be stored in map
            if (Account.createAccount(userField.getText(), passwordField.getText(), nameField.getText(), new String[]{sq1.getText(), sq2.getText(), sq3.getText()})) {
                alert("Account Created", "Account Successfully Created", "You can now login with" +
                        " the provided information", Alert.AlertType.CONFIRMATION);
                ((Node) (event.getSource())).getScene().getWindow().hide();
            } else {
                errorAlert("Creation Failed", "Account Creation Failed", "Account creation " +
                        "failed for an unknown reason.");
            }
        }
    }

    //To clean up the code a bit

    /**
     *
     * @param title
     * @param header
     * @param content
     */
    private void errorAlert(String title, String header, String content) {
        alert(title, header, content, Alert.AlertType.ERROR);
    }

    /**
     *
     * @param title
     * @param header
     * @param content
     * @param type
     */
    private void alert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

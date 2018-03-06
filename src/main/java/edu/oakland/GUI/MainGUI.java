package edu.oakland.GUI;

import edu.oakland.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class MainGUI {

    private String accountName;


    @FXML
    private TextField oldPasswordField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private TextField verifyPasswordField;

    @FXML
    private Button updatePasswordButton;


    @FXML
    private GridPane calendarGridPane;

    @FXML
    public void initialize() {

        //Create labels for day of week header
        int columnIndex = 0; //Which column to put the next label in
        for (int i = 6; i < 13; i++) { //Each day of the week by number, want to start at sunday so numbering is offset
            DayOfWeek dayOfWeek = DayOfWeek.of((i % 7) + 1); //Start at sunday, which is day 7
            Label DoWLabel = new Label();
            DoWLabel.setText(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US));
            calendarGridPane.add(DoWLabel, columnIndex++, 0);
            GridPane.setHalignment(DoWLabel, HPos.CENTER);
            GridPane.setValignment(DoWLabel, VPos.BOTTOM);
        }

        //Add each day of month to grid
        ZonedDateTime firstOfMonth = ZonedDateTime.now().withDayOfMonth(1);
        int rowIndex = 1;
        columnIndex = firstOfMonth.getDayOfWeek().getValue() % 7;

        LocalDate current = firstOfMonth.toLocalDate();
        while (current.getMonth() == firstOfMonth.getMonth()) {
            Label DoMLabel = new Label();
            DoMLabel.setText(current.format(DateTimeFormatter.ofPattern("d")));

            if (current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                rowIndex++;
            }
            calendarGridPane.add(DoMLabel, columnIndex++ % 7, rowIndex);
            GridPane.setValignment(DoMLabel, VPos.TOP);
            GridPane.setHalignment(DoMLabel, HPos.LEFT);

            current = current.plusDays(1);
        }
    }



    public void setAccountName(String accountName){
        this.accountName=accountName;
    }

    private void changePassword(ActionEvent event) {
        if (Account.login((accountName), oldPasswordField.getText())) {
            if (newPasswordField.getText() == verifyPasswordField.getText()) {
                try {
                        Account.passwordChange(accountName,newPasswordField.getText());



                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("This will not do.");
                    alert.setHeaderText("Oh no. There was an error changing the password!");
                    alert.setContentText(e.getMessage());

                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("This will not do.");
                alert.setHeaderText("Try again, friend.");
                alert.setContentText("Passwords do not match");

                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("Incorrect Current Password");

            alert.showAndWait();
        }
    }

}

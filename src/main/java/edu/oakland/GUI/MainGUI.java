package edu.oakland.GUI;

import edu.oakland.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainGUI {

    private transient static final Logger logger = Logger.getLogger(MainGUI.class.getName());

    private Account currentAccount;

    /**
     * Currently displayed month
     */
    private ZonedDateTime currentMonth;

    /**
     * GUI items displayed in this month which will need to be cleared if the month changes
     */
    private Set<Node> currentMonthNodes = new HashSet<>();

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField verifyPasswordField;

    @FXML
    private Button updatePasswordButton, logoutButton;


    @FXML
    private GridPane calendarGridPane;

    @FXML
    private Label calendarHeaderLabel, dateLabel;

    /* Add Event Page */
    @FXML
    private Button addEventSubmitButton;

    @FXML
    private DatePicker startDateField, endDateField;

    @FXML
    private TextField eventNameField, eventDescriptionField, eventLocationField, eventAttendeesField;

    public int[][] daylayout;

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
        viewMonth(ZonedDateTime.now());
    }

    private void viewMonth(ZonedDateTime theMonth) {
        calendarGridPane.getChildren().removeAll(currentMonthNodes);
        daylayout = new int[6][7];
        currentMonth = theMonth.withDayOfMonth(1);
        int rowIndex = 1;
        int columnIndex = currentMonth.getDayOfWeek().getValue() % 7; //Sunday -> 0

        calendarHeaderLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM YYYY")));

        LocalDate current = currentMonth.toLocalDate();
        while (current.getMonth() == currentMonth.getMonth()) { //For every day of month
            //When reached sunday (the first day of week) move down a row
            if (current.getDayOfWeek() == DayOfWeek.SUNDAY && current.getDayOfMonth() != 1) {
                rowIndex++;
            }

            //Label for date of month
            Label DoMLabel = new Label();
            DoMLabel.setText(current.format(DateTimeFormatter.ofPattern("d")));

            int currC = columnIndex++ % 7;
            int currR = rowIndex;

            calendarGridPane.add(DoMLabel, currC, currR);
            currentMonthNodes.add(DoMLabel);
            GridPane.setValignment(DoMLabel, VPos.TOP);
            GridPane.setHalignment(DoMLabel, HPos.LEFT);

            //todo Event display logic probably goes here
            daylayout[currR-1][currC] = Integer.valueOf(DoMLabel.getText());
            current = current.plusDays(1);
        }
    }

    @FXML
    private void viewMonthPrevious() {
        viewMonth(currentMonth.minusMonths(1));
    }

    @FXML
    private void viewMonthNext() {
        viewMonth(currentMonth.plusMonths(1));
    }

    @FXML
    private void changePassword(ActionEvent event) {
        //Check that new password boxes match
        if (newPasswordField.getText().equals(verifyPasswordField.getText())) {
            boolean success = false;
            try {
                //Try and change the password
                success = getCurrentAccount().changePassword(oldPasswordField.getText(), newPasswordField.getText());

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("This will not do.");
                alert.setHeaderText("Oh no. There was an error changing the password!");
                alert.setContentText(e.getMessage());
                logger.log(Level.SEVERE, "Can't make password hash", e);

                alert.showAndWait();
            }
            if (success) {
                oldPasswordField.setText("");
                newPasswordField.setText("");
                verifyPasswordField.setText("");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Woo-hoo!");
                alert.setHeaderText("Your password has been changed!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("This will not do.");
                alert.setHeaderText("Try again, friend.");
                alert.setContentText("Passwords do not match");
                alert.setContentText("Incorrect Current Password");

                alert.showAndWait();
            }

        } else { //New passwords didn't match
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("Passwords do not match");

            alert.showAndWait();
        }
    }

    //TODO

    /**
     * @param event
     */
    @FXML
    private void tryLogout(ActionEvent event) {
        try {
            java.net.URL resource = getClass().getClassLoader().getResource("LoginGUI.fxml");
            if (resource == null) {
                resource = getClass().getResource("LoginGUI.fxml");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Cadmium Calendar");
            stage.setScene(new Scene(root, 600, 300));
            stage.show();

            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //rows 0 to 6, where 0 is Sunday and 6 is Saturday
    //columns 0 to 6, where 0 is the first week in the calendar and 6 is the last
    @FXML
    private void getCellData(MouseEvent e) {
        Node source = (Node) e.getSource();

        //Retrieves the position from the [6,7] grids
        Integer columnVal = (GridPane.getColumnIndex(source) == null) ? 0 : (GridPane.getColumnIndex(source));
        Integer rowVal = (GridPane.getRowIndex(source) == null) ? 0 : (GridPane.getRowIndex(source));

        //Bad programming
        //Offset for 7x7 grid
        rowVal -= 1;

        //Collects date that matches the 7x7 grid
        Integer curdate = daylayout[rowVal][columnVal];

        //prevents throwing an DateTimeException if the column value = 0 (sunday needs to be 7)
        if (columnVal == 0) {
            columnVal = 7;
        }

        if(curdate < 0){
            //TODO change month also doesn't work
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Wrong month selected");
            alert.showAndWait();
        }else if(curdate == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Wrong month selected");
            alert.showAndWait();
        }else{
            String output = (DayOfWeek.of(columnVal) + " " + currentMonth.getMonth() + " " + curdate);
            dateLabel.setText(output);
        }

    }

    @FXML
    private void submitEvent(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Test");
        alert.setHeaderText("Test");
        alert.setContentText("Unimplemented");
        alert.showAndWait();
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }
}

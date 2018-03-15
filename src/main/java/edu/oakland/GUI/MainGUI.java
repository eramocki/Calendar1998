package edu.oakland.GUI;

import edu.oakland.Account;
import edu.oakland.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

public class MainGUI {

    private transient static final Logger logger = Logger.getLogger(MainGUI.class.getName());

    private Account currentAccount;

    /**
     * Currently displayed month
     */
    private ZonedDateTime currentMonth;

    private HashMap<LocalDate, Node> nodesByDayNumber = new HashMap<>();
    private HashMap<LocalDate, Node> nodesByDayEvent = new HashMap<>();
    private HashMap<LocalDate, Node> nodesByDayRect = new HashMap<>();

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
    private ComboBox startTimeDropdown, endTimeDropdown, recurField;

    @FXML
    private TextField eventNameField, eventDescriptionField, eventLocationField, eventAttendeesField;

    @FXML
    private TextArea eventOutput;

    @FXML
    private CheckBox allDay, highPrior;

    public int[][] daylayout;

    @FXML
    public void initialize() {
    }

    public void postInit() {
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
        Event dummyEvent = new Event(ZonedDateTime.now(), ZonedDateTime.now().plusSeconds(120), "Dummy Event");
        getCurrentAccount().calendar.addEvent(dummyEvent);
        viewMonth(ZonedDateTime.now());

        setupTimeCombobox(startTimeDropdown, LocalTime.MIDNIGHT);
        setupTimeCombobox(endTimeDropdown, LocalTime.MIDNIGHT.plusSeconds(1));
        startTimeDropdown.getSelectionModel().selectFirst();
        endTimeDropdown.getSelectionModel().selectFirst();
        editStartDate(startDateField,LocalDate.now());
        editEndDate(endDateField,LocalDate.now());

        recurField.getItems().removeAll(recurField.getItems());
        recurField.getItems().addAll("Never", "Daily", "Weekly", "Monthly", "Yearly");
        recurField.getSelectionModel().selectFirst();
    }

    private void setupTimeCombobox(ComboBox theComboBox, LocalTime selected) {
        setupTimeCombobox(theComboBox, selected, Duration.ofMinutes(30));
    }

    private void setupTimeCombobox(ComboBox theComboBox, LocalTime selected, Duration offset) {
        theComboBox.getItems().clear();

        LocalTime current = LocalTime.MIDNIGHT;
        theComboBox.getItems().add(current.format(DateTimeFormatter.ofPattern("HH:mm")));

        while (current.isBefore(current.plus(offset))) {
            theComboBox.getItems().add(current.plus(offset).format(DateTimeFormatter.ofPattern("HH:mm")));
            current = current.plus(offset);
        }

        theComboBox.setValue(selected.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void editStartDate(DatePicker theStartDateField, LocalDate selected){
        theStartDateField.setValue(selected);
    }

    private void editEndDate(DatePicker theEndDateField, LocalDate selected){
        theEndDateField.setValue(selected);
    }

    private void viewMonth(ZonedDateTime theMonth) {
        calendarGridPane.getChildren().removeAll(nodesByDayNumber.values());
        calendarGridPane.getChildren().removeAll(nodesByDayEvent.values());
        calendarGridPane.getChildren().removeAll(nodesByDayRect.values());

        //todo cache?
        nodesByDayNumber.clear();
        nodesByDayEvent.clear();
        nodesByDayRect.clear();
        daylayout = new int[6][7];
        currentMonth = theMonth.withDayOfMonth(1);
        int rowIndex = 1;
        int columnIndex = currentMonth.getDayOfWeek().getValue() % 7; //Sunday -> 0

        calendarHeaderLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM YYYY")));
        YearMonth yearMonth = YearMonth.of(currentMonth.getYear(), currentMonth.getMonth());
        Set<Event> monthEvents = getCurrentAccount().calendar.getMonthEvents(yearMonth);

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

            Iterator<Event> ir = monthEvents.iterator();
            while (ir.hasNext()) {
                Event currEvent = ir.next();
                if(currEvent.getStart().getDayOfMonth() == current.getDayOfMonth())
                {
                    //TODO offset new events if they share the same space
                    javafx.scene.shape.Rectangle rectangle = new javafx.scene.shape.Rectangle(0, 0, 75, 10);
                    rectangle.setFill(Color.ANTIQUEWHITE);
                    Label eventLabel = new Label();
                    eventLabel.setText(currEvent.getEventName());

                    calendarGridPane.add(rectangle, currC, currR);
                    nodesByDayRect.put(current, rectangle);
                    calendarGridPane.add(eventLabel, currC, currR);
                    nodesByDayEvent.put(current, eventLabel);
                }
            }

            calendarGridPane.add(DoMLabel, currC, currR);
            nodesByDayNumber.put(current, DoMLabel);
            GridPane.setValignment(DoMLabel, VPos.TOP);
            GridPane.setHalignment(DoMLabel, HPos.LEFT);

            //todo Event display logic probably goes here
            daylayout[currR - 1][currC] = Integer.valueOf(DoMLabel.getText());
            current = current.plusDays(1);
        }

        //Edits the first row of the array to have values of -1, which indicate those days take place in the prior month
        for (int i = 0; daylayout[0][i] == 0; i++) {
            daylayout[0][i] = -1;
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
                logger.log(Level.SEVERE, "Can't change password", e);

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
        setCurrentAccount(null);
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

    /**
     * Prints the current Year, month, and date to the right side panel of the calendar based on the date clicked.
     * Clicking on unlabeled date prior to the month will cause the calendar to go back a month, and clicking ahead will
     * move the calendar a month ahead.
     *
     * @param e Event on mouseclick
     */
    @FXML
    private void getCellData(MouseEvent e) {
        Account acc = getCurrentAccount();
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

        if (curdate == -1) {
            viewMonthPrevious();
            dateLabel.setText("");
            eventOutput.clear();
            eventOutput.setDisable(true);
        } else if (curdate == 0) {
            viewMonthNext();
            dateLabel.setText("");
            eventOutput.clear();
            eventOutput.setDisable(true);
        } else {
            String output = (DayOfWeek.of(columnVal) + " " + currentMonth.getMonth() + " " + curdate);
            dateLabel.setText(output);
            LocalDate disDate = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), curdate);
            //System.out.print(disDate);
            searchEvent(disDate);
        }
    }

    @FXML
    //move to calendar class
    private void searchEvent(LocalDate localDate) {
        //search treeset for localdate, if true then print to textarea
        eventOutput.setDisable(false);

    }

    @FXML
    private void submitEvent(ActionEvent event) {


        String eventName = eventNameField.getText();
        String eventDesc = eventDescriptionField.getText();
        String eventLoc = eventLocationField.getText();
        String eventAttendees = eventAttendeesField.getText();
        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();
        Boolean isAllDay = allDay.isSelected();
        Boolean isPriority = highPrior.isSelected();
        String startingTime = startTimeDropdown.getSelectionModel().getSelectedItem().toString();
        String endingTime = endTimeDropdown.getSelectionModel().getSelectedItem().toString();
        String recurState = recurField.getSelectionModel().getSelectedItem().toString();

        //TODO Pass these variables to the actual calendar for adding


        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Test");
        alert.setHeaderText("Test");
        alert.setContentText("Event Name: " + eventName + "\nEvent Description: " + eventDesc + "\nLocation: " + eventLoc + "\nAttendees: " + eventAttendees + "\nFrom " + startDate.toString() + " to " + endDate.toString() + "\nAll Day? " + isAllDay + ", High Priority? " + isPriority + "\nFrom " + startingTime + " to " + endingTime + "\nRecurring type: " + recurState);
        alert.showAndWait();
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }
}
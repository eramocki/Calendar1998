package edu.oakland.GUI;

import edu.oakland.Account;
import edu.oakland.Event;
import edu.oakland.Frequency;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Iterator;
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
     * Currently displayed day in detail pane
     */
    private LocalDate currentDate;

    /**
     * Each day in the calendar view has a VBox to put things in
     */
    private HashMap<LocalDate, VBox> VBoxesByDay = new HashMap<>();

    /**
     * Each event has a label made for it
     */
    private HashMap<Label, Event> eventsByLabel = new HashMap<>();

//    private HashMap<LocalDate, Node> nodesByDayNumber = new HashMap<>();
//    private HashMap<LocalDate, Node> nodesByDayEvent = new HashMap<>();
//    private HashMap<LocalDate, Node> nodesByDayRect = new HashMap<>();

    @FXML
    private TabPane tabPane;

    @FXML
    private TextArea eventOutput;

    public int[][] daylayout;

    public Event eventPointer;

    @FXML
    private Button updateButton;

    @FXML
    private GridPane calendarGridPane;

    @FXML
    private Label calendarHeaderLabel, dateLabel;

    /*  Update Event page */

    @FXML
    private DatePicker startDateFieldUpdate, endDateFieldUpdate;

    @FXML
    private ComboBox startTimeDropdownUpdate, endTimeDropdownUpdate, recurFieldUpdate;

    @FXML
    private TextField eventNameFieldUpdate, eventDescriptionFieldUpdate, eventLocationFieldUpdate, eventAttendeesFieldUpdate;

    @FXML
    private CheckBox allDayUpdate, highPriorUpdate;

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
    private CheckBox allDay, highPrior;

    /* Settings Page */

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField verifyPasswordField;

    @FXML
    private Button updatePasswordButton, logoutButton;

    @FXML
    public void initialize() { }

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
        Event dummyEvent1 = new Event(ZonedDateTime.now(), ZonedDateTime.now().plusSeconds(120), "Event 1");
        dummyEvent1.setHighPriority(true);
        Event dummyEventa = new Event(ZonedDateTime.now().plusMinutes(5), ZonedDateTime.now().plusMinutes(120), "Event 1.5");
        Event dummyEvent2 = new Event(ZonedDateTime.now().plusDays(2), ZonedDateTime.now().plusDays(3), "Event 2");
        Event dummyEvent3 = new Event(ZonedDateTime.now().plusDays(2).plusSeconds(1), ZonedDateTime.now().plusDays(3).plusMinutes(1), "Event 3");
        Event dummyEvent4 = new Event(ZonedDateTime.now().minusDays(7), ZonedDateTime.now().minusDays(3), "Event 4");
        getCurrentAccount().calendar.addEvent(dummyEvent1);
        getCurrentAccount().calendar.addEvent(dummyEventa);
        getCurrentAccount().calendar.addEvent(dummyEvent2);
        getCurrentAccount().calendar.addEvent(dummyEvent3);
        getCurrentAccount().calendar.addEvent(dummyEvent4);
        viewMonth(ZonedDateTime.now());

        setupTimeCombobox(startTimeDropdown, LocalTime.MIDNIGHT);
        setupTimeCombobox(endTimeDropdown, LocalTime.MIDNIGHT.plusSeconds(1));
        //setupTimeCombobox(startTimeDropdownUpdate, LocalTime.MIDNIGHT);
        //setupTimeCombobox(endTimeDropdownUpdate, LocalTime.MIDNIGHT.plusSeconds(1));
        startTimeDropdown.getSelectionModel().selectFirst();
        endTimeDropdown.getSelectionModel().selectFirst();
        //startTimeDropdownUpdate.getSelectionModel().selectFirst();
        //endTimeDropdownUpdate.getSelectionModel().selectFirst();
        editStartDate(startDateField, LocalDate.now());
        editEndDate(endDateField, LocalDate.now());
        //editStartDate(startDateFieldUpdate, LocalDate.now());
        //editEndDate(endDateFieldUpdate, LocalDate.now());

        recurField.getItems().removeAll(recurField.getItems());
        recurField.getItems().addAll("Never", "Daily", "Weekly", "Monthly", "Yearly");
        recurField.getSelectionModel().selectFirst();
        //TODO update recur
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

    private void editStartDate(DatePicker theStartDateField, LocalDate selected) {
        theStartDateField.setValue(selected);
    }

    private void editEndDate(DatePicker theEndDateField, LocalDate selected) {
        theEndDateField.setValue(selected);
    }

    private void viewMonth(ZonedDateTime theMonth) {
        calendarGridPane.getChildren().removeAll(VBoxesByDay.values());
        //todo cache?
        VBoxesByDay.clear();
        eventsByLabel.clear();

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

            //The VBox will hold any GUI things for the day
            VBox dayVBox = new VBox();
            dayVBox.setPadding(new Insets(1));
            dayVBox.setOnMouseClicked(this::getCellData);

            //Label for date of month
            Label DoMLabel = new Label();
            DoMLabel.setText(current.format(DateTimeFormatter.ofPattern("d")));
            dayVBox.getChildren().add(DoMLabel);

            int currC = columnIndex++ % 7;
            int currR = rowIndex;

            Set<Event> dayEvents = getCurrentAccount().calendar.getDayEvents(current);
            Iterator<Event> ir = dayEvents.iterator();
            while (ir.hasNext()) {
                Event currEvent = ir.next();

                Label eventLabel = new Label();
                eventLabel.setText(currEvent.getEventName());
                if(currEvent.getHighPriority()) {
                    eventLabel.setStyle("-fx-background-color: OrangeRed;");
                }else{
                    eventLabel.setStyle("-fx-background-color: AntiqueWhite;");
                }

                eventLabel.setMaxWidth(Double.MAX_VALUE); //So it fills the width
                eventLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, this::viewEventDetail);
                eventsByLabel.put(eventLabel, currEvent);

                dayVBox.getChildren().add(eventLabel);
            }

            calendarGridPane.add(dayVBox, currC, currR);
            VBoxesByDay.put(current, dayVBox);

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
    private void viewEventDetail(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        Label source;
        try {
            source = (Label) sourceNode;
        } catch (ClassCastException e) {
            logger.log(Level.WARNING, "Event came from unexpected source (not a label)", e);
            return;
        }
        if (eventsByLabel.containsKey(source)) {
            displayEventDetail(eventsByLabel.get(source));
        } else {
            logger.log(Level.WARNING, "Event came from unexpected source or label got removed");
            return;
        }
        event.consume(); //Prevent the event from being propogated up through normal channels
        //FX normally calls getCellData again if event.consume was not called, but in that case
        //the event source is set to the VBox, not the label, so we were overwriting
        //the stuff shown in the right pane. By manually calling getCellDate ourselves,
        //the source is still the Label and we can handle better based on that
        getCellData(event);
    }

    private void displayEventDetail(Event theEvent) {
        eventPointer = theEvent;
        eventOutput.setDisable(false);
        eventOutput.setText(theEvent.getEventName());
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

    private void printToView(Event tempEvent) {
        eventPointer = tempEvent;
        Set<Event> setOfDayEvent = getCurrentAccount().calendar.getDayEvents(currentDate);
        if (setOfDayEvent.isEmpty()) {
            eventOutput.setDisable(true);
            eventOutput.setText("");
        } else {
            Iterator<Event> ir = setOfDayEvent.iterator();
            if (ir.hasNext()) {
                eventPointer = ir.next();
                if (eventPointer != null) {
                    String temp = "";
                    temp += (eventPointer.getEventName());
                    if (eventPointer.getEventDesc() != null) {
                        temp += ("\n" + eventPointer.getEventDesc());
                    }
                    if (eventPointer.getEventLocation() != null) {
                        temp +=("\n" + eventPointer.getEventLocation());
                    }
                    if (eventPointer.getEventAttendees() != null) {
                        temp +=("\n" + eventPointer.getEventAttendees());
                    }
                    //TODO list time/date info etc
                    //temp.append("\nStart Date: " + eventPointer.getStart().)

                    eventOutput.setText(temp);
                    eventOutput.setDisable(false);
                } else {
                    eventOutput.setText("");
                    eventOutput.setDisable(true);

                }
            }
        }
        viewMonth(currentMonth);
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
        /*
         * If an event label is pressed the source is a Label,
         * but GridPane.getIndex needs the VBox that is in the cell to get row and column numbers
         */
        Node origSource = (Node) e.getSource();
        Node source = (Node) e.getSource();

        if (origSource instanceof Label) {
            //An event label was pressed
            source = origSource.getParent();
        }

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
            currentDate = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), curdate);
            Set<Event> dayEvents = getCurrentAccount().calendar.getDayEvents(currentDate);
            if (dayEvents.isEmpty()) {
                eventOutput.setDisable(true);
                eventOutput.setText("");
            } else {
                //If an event label was pressed we shouldn't overwrite the event it already displayed
                if (!(origSource instanceof Label)) {
                    //It doesn't matter what event we show
                    displayEventDetail(dayEvents.iterator().next());
                }
            }
        }
    }

    @FXML
    private void viewNextEvent(ActionEvent event) {
        printToView(eventPointer);
    }

    @FXML
    private void viewPreviousEvent(ActionEvent event) {
        //TODO
        System.out.println("TODO");
    }

    @FXML
    private void deleteE(ActionEvent event) {
        try {
            getCurrentAccount().calendar.removeEvent(eventPointer);
            printToView(eventPointer);
        } catch (NullPointerException e) {
            System.out.println("Nothing to delete");
        }
    }

    @FXML
    private void openUpdate(ActionEvent event) {
        if(eventPointer != null) {
            Stage stage;
            try {
                stage = new Stage();
                java.net.URL resource = getClass().getClassLoader().getResource("UpdateGUI.fxml");
                if (resource == null) {
                    resource = getClass().getResource("UpdateGUI.fxml");
                }
                Parent root4 = FXMLLoader.load(resource);
                stage.setScene(new Scene(root4, 800, 650));
                stage.setTitle("Update Event " + eventPointer.getEventName());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(updateButton.getScene().getWindow());
                stage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("You don't have an event selected");
        }

    }

    //unfinished
    @FXML
    private void modifyEvent(ActionEvent event) {

        //Doesn't work
        eventNameFieldUpdate.setText(eventPointer.getEventName());
        eventDescriptionFieldUpdate.setText(eventPointer.getEventDesc());
        eventLocationFieldUpdate.setText(eventPointer.getEventLocation());
        eventAttendeesFieldUpdate.setText(eventPointer.getEventAttendees());

        LocalDate startDateUpdate = startDateFieldUpdate.getValue();
        LocalDate endDateUpdate = endDateFieldUpdate.getValue();
        String startingTimeUpdate = startTimeDropdownUpdate.getSelectionModel().getSelectedItem().toString();
        String endingTimeUpdate = endTimeDropdownUpdate.getSelectionModel().getSelectedItem().toString();

        String[] splitStartHM = startingTimeUpdate.split(":");
        String[] splitEndHM = endingTimeUpdate.split(":");

        ZonedDateTime start = ZonedDateTime.of(startDateUpdate.getYear(), startDateUpdate.getMonthValue(), startDateUpdate.getDayOfMonth(), Integer.parseInt(splitStartHM[0]), Integer.parseInt(splitStartHM[1]), 0, 0, ZoneId.systemDefault());
        ZonedDateTime end = ZonedDateTime.of(endDateUpdate.getYear(), endDateUpdate.getMonthValue(), endDateUpdate.getDayOfMonth(), Integer.parseInt(splitEndHM[0]), Integer.parseInt(splitEndHM[1]), 0, 0, ZoneId.systemDefault());

        if(end.compareTo(start) == -1 || endingTimeUpdate.compareTo(endingTimeUpdate) == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("You can't have your end date/time happen in the past!");

            alert.showAndWait();
        }else if(eventNameField.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("Your event name cannot be blank");

            alert.showAndWait();
        }else{
            Event updateEvent = new Event(start, end, eventNameFieldUpdate.getText());
            updateEvent.setEventDesc(eventDescriptionFieldUpdate.getText());
            updateEvent.setEventLocation(eventAttendeesFieldUpdate.getText());
            updateEvent.setEventAttendees(eventAttendeesFieldUpdate.getText());
            updateEvent.setEventAllDay(allDayUpdate.isSelected());
            updateEvent.setHighPriority(highPriorUpdate.isSelected());
            updateEvent.setFrequency(Frequency.valueOf(recurFieldUpdate.getSelectionModel().getSelectedItem().toString().toUpperCase()));
            getCurrentAccount().calendar.addEvent(updateEvent);
            viewMonth(currentMonth);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Event was updated!.");
            alert.setHeaderText("Well done");
            alert.setContentText("Your event has been update on the calendar");
            alert.showAndWait();
            SingleSelectionModel<Tab> selector = tabPane.getSelectionModel();
            selector.selectFirst();
        }
    }



    @FXML
    private void submitEvent(ActionEvent event) {

        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();
        String startingTime = startTimeDropdown.getSelectionModel().getSelectedItem().toString();
        String endingTime = endTimeDropdown.getSelectionModel().getSelectedItem().toString();
        String[] splitStartHM = startingTime.split(":");
        String[] splitEndHM = endingTime.split(":");

        ZonedDateTime start = ZonedDateTime.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth(), Integer.parseInt(splitStartHM[0]), Integer.parseInt(splitStartHM[1]), 0, 0, ZoneId.systemDefault());
        ZonedDateTime end = ZonedDateTime.of(endDate.getYear(), endDate.getMonthValue(), endDate.getDayOfMonth(), Integer.parseInt(splitEndHM[0]), Integer.parseInt(splitEndHM[1]), 0, 0, ZoneId.systemDefault());

        ZonedDateTime min = ZonedDateTime.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth(), LocalTime.MIN.getHour(), LocalTime.MIN.getMinute(), 0, 0, ZoneId.systemDefault());
        ZonedDateTime max = ZonedDateTime.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth(), LocalTime.MAX.getHour(), LocalTime.MAX.getMinute(), 0, 0, ZoneId.systemDefault());


        if(end.compareTo(start) == -1 || endingTime.compareTo(endingTime) == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("You can't have your end date/time happen in the past!");

            alert.showAndWait();
        }else if(eventNameField.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("Your event name cannot be blank");
            alert.showAndWait();
        }else{
            Event disEvent = new Event(start, end, eventNameField.getText());
            disEvent.setEventAllDay(allDay.isSelected());
            if(allDay.isSelected())
            {
                disEvent.setStart(min);
                disEvent.setEnd(max);
            }
            disEvent.setEventDesc(eventDescriptionField.getText());
            disEvent.setEventLocation(eventAttendeesField.getText());
            disEvent.setEventAttendees(eventAttendeesField.getText());
            disEvent.setHighPriority(highPrior.isSelected());
            disEvent.setFrequency(Frequency.valueOf(recurField.getSelectionModel().getSelectedItem().toString().toUpperCase()));
            getCurrentAccount().calendar.addEvent(disEvent);
            viewMonth(currentMonth);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Event was created!.");
            alert.setHeaderText("Well done");
            alert.setContentText("Your event has been added to the calendar");
            alert.showAndWait();
            SingleSelectionModel<Tab> selector = tabPane.getSelectionModel();
            selector.selectFirst();
        }
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }
}
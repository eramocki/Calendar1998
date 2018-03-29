package edu.oakland.GUI;

import edu.oakland.*;
import javafx.application.Platform;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.DAYS;

public class MainGUI {

    private static final Logger logger = Logger.getLogger(MainGUI.class.getName());

    private Account currentAccount;

    private ZonedDateTime currentMonth;

    /**
     * Currently displayed day in detail pane
     */
    private LocalDate currentDate;

    /**
     * Currently selected event in detail pane
     */
    private Event currentEvent;

    /**
     * Each day in the calendar view has a VBox to put things in
     */
    private HashMap<LocalDate, VBox> VBoxesByDay = new HashMap<>();

    /**
     * Each event has a label made for it
     */
    private HashMap<Label, Event> eventsByLabel = new HashMap<>();

    @FXML
    private TabPane tabPane;

    @FXML
    private TextArea eventOutput;

    private int[][] daylayout;

    @FXML
    private ToggleButton toggleCompleted;

    @FXML
    private Button updateButton, removeButton;

    @FXML
    private GridPane calendarGridPane;

    @FXML
    private Label calendarHeaderLabel, dateLabel;

    @FXML
    private MenuBar myMenuBar;


    /* Add Event Page */

    @FXML
    private DatePicker startDateField, endDateField;

    @FXML
    private ComboBox startTimeDropdown, endTimeDropdown, recurField;

    @FXML
    private TextField eventNameField, eventLocationField, eventAttendeesField;

    @FXML
    private TextArea eventDescriptionField;

    @FXML
    private CheckBox allDay, highPrior;

    @FXML
    private TextArea completedEventsArea;

    private LocalDate oldDateValue;

    @FXML
    public void initialize() {
        //NB Only pure GUI setup! Others use postInit

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

        //Set the time comboboxes with good values
        GUIHelper.setupTimeCombobox(startTimeDropdown, LocalTime.MIDNIGHT);
        GUIHelper.setupTimeCombobox(endTimeDropdown, LocalTime.MIDNIGHT.plusSeconds(1));
        startTimeDropdown.getSelectionModel().selectFirst();
        endTimeDropdown.getSelectionModel().selectFirst();

        //Set the date fields
        oldDateValue = LocalDate.now();
        endDateField.setValue(oldDateValue);
        startDateField.setValue(LocalDate.now());

        //Set the recurrence dropdown
        recurField.getItems().clear();
        for (Frequency freq : Frequency.values()) {
            recurField.getItems().add(freq);
        }
        recurField.getSelectionModel().selectFirst();
    }

    public void postInit() {
        Set<SingularEvent> dummyEvents = new HashSet<>();

        if (getCurrentAccount().getUserName().equals("y")) {
            getCurrentAccount().getCalendar().getMonthEvents(YearMonth.now())
                    .forEach(getCurrentAccount().getCalendar()::removeEvent);

            SingularEvent dummyEvent1 = new SingularEvent(ZonedDateTime.now(),
                    ZonedDateTime.now().plusSeconds(120),
                    "High Prio SingularEvent");
            dummyEvent1.setHighPriority(true);
            dummyEvent1.setEventDesc("Description");
            dummyEvents.add(dummyEvent1);
            dummyEvents.add(new SingularEvent(ZonedDateTime.now().plusMinutes(5),
                    ZonedDateTime.now().plusMinutes(120),
                    "SingularEvent 1.5"));
            dummyEvents.add(new SingularEvent(ZonedDateTime.now().plusDays(2),
                    ZonedDateTime.now().plusDays(3),
                    "SingularEvent 123"));
            dummyEvents.add(new SingularEvent(ZonedDateTime.now().plusDays(2).plusSeconds(1),
                    ZonedDateTime.now().plusDays(3).plusMinutes(1),
                    "After 123"));
            dummyEvents.add(new SingularEvent(ZonedDateTime.now().minusDays(7),
                    ZonedDateTime.now().minusDays(3),
                    "LongEvent"));
            dummyEvents.add(new SingularEvent(ZonedDateTime.now().minusDays(8).plusHours(6),
                    ZonedDateTime.now().minusDays(6).plusHours(6),
                    "Overlap 1"));
            dummyEvents.add(new SingularEvent(ZonedDateTime.now().minusDays(8),
                    ZonedDateTime.now().minusDays(6).plusHours(6),
                    "Overlap 2"));
            dummyEvents.add(new SingularEvent(ZonedDateTime.now().minusDays(6),
                    ZonedDateTime.now().minusDays(4),
                    "48HrEvent"));

            RecurrentEvent dummyEventRecurring = new RecurrentEvent(ZonedDateTime.now().minusWeeks(2),
                    ZonedDateTime.now().minusWeeks(2).plusMinutes(30),
                    "repeating event",
                    Frequency.DAILY,
                    ZonedDateTime.now().minusWeeks(2),
                    ZonedDateTime.now().minusWeeks(1));
            dummyEvents.add(dummyEventRecurring);
            dummyEvent1.setCompleted(true);

            dummyEvents.forEach(getCurrentAccount().getCalendar()::addEvent);
        }
        toggleCompleted.setDisable(true);
        updateButton.setDisable(true);
        removeButton.setDisable(true);
        viewMonth(ZonedDateTime.now());
    }


    public void viewMonth(ZonedDateTime theMonth) {
        calendarGridPane.getChildren().removeAll(VBoxesByDay.values());
        //todo cache?
        VBoxesByDay.clear();
        eventsByLabel.clear();

        daylayout = new int[7][7];
        currentMonth = theMonth.withDayOfMonth(1);
        int rowIndex = 1;
        int columnIndex = currentMonth.getDayOfWeek().getValue() % 7; //Sunday -> 0

        calendarHeaderLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM YYYY")));
        //YearMonth yearMonth = YearMonth.of(currentMonth.getYear(), currentMonth.getMonth());
        //Set<Event> monthEvents = getCurrentAccount().getCalendar().getMonthEvents(yearMonth);

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

            Set<Event> dayEvents = getCurrentAccount().getCalendar().getDayEvents(current);
            for (Event currEvent : dayEvents) {
                Label eventLabel = new Label();
                eventLabel.setText(currEvent.getEventName());
                if (currEvent.getHighPriority()) {
                    eventLabel.setStyle("-fx-background-color: OrangeRed;");
                } else {
                    eventLabel.setStyle("-fx-background-color: AntiqueWhite;");
                }

                eventLabel.setMaxWidth(Double.MAX_VALUE); //So it fills the width
                eventLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, this::viewEventDetail);
                eventsByLabel.put(eventLabel, currEvent);

                dayVBox.getChildren().add(eventLabel);
            }

            calendarGridPane.add(dayVBox, currC, currR);
            VBoxesByDay.put(current, dayVBox);

            daylayout[currR][currC] = Integer.valueOf(DoMLabel.getText());
            current = current.plusDays(1);
        }
    }

    @FXML
    private void openCreateAccountGUI() {
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
            Stage oldStage = (Stage) myMenuBar.getScene().getWindow();
            stage.initOwner(oldStage.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void importData() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import data");
        chooser.setInitialDirectory(Account.getAccountFile().getParentFile());
        File importFile = chooser.showOpenDialog(new Stage());

        if (importFile == null) return;

        if (Account.loadAccounts(importFile)){
            setCurrentAccount(Account.getAccount(getCurrentAccount().getUserName())); //Refresh from what was loaded
            viewMonth(currentMonth);
            GUIHelper.alert("Load me", "Data were loaded", "Loaded data successfully!", Alert.AlertType.INFORMATION);
        } else {
            GUIHelper.errorAlert("This will not do.", "Couldn't load data!", "There was an error loading that file!");
        }
    }

    @FXML
    private void exportData() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(Account.getAccountFile().getParentFile());
        chooser.setInitialFileName("accounts.dat");
        File exportFile = chooser.showSaveDialog(null);

        if (exportFile == null) return;

        if (Account.saveAccounts(exportFile)){
            GUIHelper.alert("Save me", "Data were saved", "Exported data successfully!", Alert.AlertType.INFORMATION);
        } else {
            GUIHelper.errorAlert("This will not do.", "Couldn't save data!", "There was an error saving that file!");
        }
    }

    @FXML
    private void saveData() {
        Account.saveAccounts();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Saving Completed");
        alert.setHeaderText("Saving Completed");
        alert.setContentText("You did it! (I knew you could)");
        alert.showAndWait();
    }

    @FXML
    private void logoutApp() {
        if (currentAccount == null) {
            logger.warning("Wanted to logout but the current user was already null");
            return;
        }
        try {
            java.net.URL resource = getClass().getClassLoader().getResource("LoginGUI.fxml");
            if (resource == null) {
                resource = getClass().getResource("LoginGUI.fxml");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Parent root = fxmlLoader.load();
            Stage oldStage = (Stage) myMenuBar.getScene().getWindow();
            oldStage.close();
            setCurrentAccount(null);

            Stage newStage = new Stage();
            newStage.setTitle("Cadmium Calendar");
            newStage.setScene(new Scene(root, 400, 400));
            newStage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Couldn't make a new loginGUI so can't logout", e);
        }
    }

    @FXML
    private void exitApp() { Platform.exit(); }

    @FXML
    private void gotoAddEventTab() {
        SingleSelectionModel<Tab> selector = tabPane.getSelectionModel();
        selector.select(1);
    }

    @FXML
    private void openSettingsGUI() {
        try {
            java.net.URL resource = getClass().getClassLoader().getResource("SettingsGUI.fxml");
            if (resource == null) {
                resource = getClass().getResource("SettingsGUI.fxml");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Settings");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tabPane.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aboutApp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cadmium Calendar");
        alert.setHeaderText("Copyright 2018");
        alert.setContentText(
                "Created by:\nIsida Ndreu\nJustin Kur\nSean Ramocki\nEric Ramocki\nJosh Baird\nMichael Koempel");
        alert.showAndWait();
    }

    @FXML
    private void viewMonthPrevious() { viewMonth(currentMonth.minusMonths(1)); }

    @FXML
    private void viewMonthNext() { viewMonth(currentMonth.plusMonths(1)); }

    @FXML
    private void viewEventDetail(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        Label source;
        try {
            source = (Label) sourceNode;
        } catch (ClassCastException e) {
            logger.log(Level.WARNING, "SingularEvent came from unexpected source (not a label)", e);
            return;
        }
        if (eventsByLabel.containsKey(source)) {
            currentEvent = eventsByLabel.get(source);
            printToView();
        } else {
            logger.log(Level.WARNING, "SingularEvent came from unexpected source or label got removed");
            return;
        }
        event.consume(); //Prevent the event from being propagated up through normal channels
        //FX normally calls getCellData again if event.consume was not called, but in that case
        //the event source is set to the VBox, not the label, so we were overwriting
        //the stuff shown in the right pane. By manually calling getCellDate ourselves,
        //the source is still the Label and we can handle better based on that
        getCellData(event);
    }

    @FXML
    private void eventCompletionStatus() {
        if (toggleCompleted.isSelected()) {
            currentEvent.setCompleted(true);
        } else {
            currentEvent.setCompleted(false);
        }
        printToView();
    }

    /**
     * Handles printing text to the side view of a selected event.
     */
    private void printToView() {
        if (currentEvent == null) {
            eventOutput.setText("");
            eventOutput.setDisable(true);
            viewMonth(currentMonth);
            return;
        }

        StringBuilder temp = new StringBuilder();
        temp.append(currentEvent.getEventName());
        ZonedDateTime st = currentEvent.getStart();
        ZonedDateTime end = currentEvent.getEnd();
        if (currentEvent.getEventDesc() != null) {
            temp.append("\n").append(currentEvent.getEventDesc());
        }
        if (currentEvent.getEventLocation() != null) {
            temp.append("\n").append(currentEvent.getEventLocation());
        }
        if (currentEvent.getEventAttendees() != null) {
            temp.append("\n").append(currentEvent.getEventAttendees());
        }
        if (!currentEvent.getEventAllDay()) {
            temp.append(st.toLocalDateTime().format(DateTimeFormatter.ofPattern("\nyyyy-MM-dd HH:mm")));
            temp.append(end.toLocalDateTime().format(DateTimeFormatter.ofPattern("\nyyyy-MM-dd HH:mm")));
        } else {
            temp.append("\nAll Day SingularEvent");
            temp.append(st.toLocalDateTime().format(DateTimeFormatter.ofPattern("\nyyyy-MM-dd")));
            temp.append(end.toLocalDateTime().format(DateTimeFormatter.ofPattern("\nyyyy-MM-dd")));
        }
        if (currentEvent.getHighPriority()) {
            temp.append("\nHigh Priority");
        }
        if (currentEvent.getFrequency().equals(Frequency.WEEKLY)) {
            temp.append("\nRecurs Weekly");
        } else if (currentEvent.getFrequency().equals(Frequency.MONTHLY)) {
            temp.append("\nRecurs Monthly");
        } else if (currentEvent.getFrequency().equals(Frequency.DAILY)) {
            temp.append("\nRecurs Daily");
        }
        temp.append("\nIs Complete? ").append(currentEvent.getCompleted());
        eventOutput.setText(temp.toString());
        eventOutput.setDisable(false);
        toggleCompleted.setDisable(false);
        updateButton.setDisable(false);
        removeButton.setDisable(false);

        if (currentEvent.getCompleted()) {
            toggleCompleted.setSelected(true);
        } else {
            toggleCompleted.setSelected(false);
        }

        viewMonth(currentMonth);
    }

    /**
     * Prints the current Year, month, and date to the right side panel of the calendar based on the date clicked.
     * Clicking on unlabeled date prior to the month will cause the calendar to go back a month, and clicking ahead will
     * move the calendar a month ahead.
     *
     * @param e SingularEvent on mouseclick
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

        Integer columnVal = (GridPane.getColumnIndex(source) == null) ? 0 : (GridPane.getColumnIndex(source));
        Integer rowVal = (GridPane.getRowIndex(source) == null) ? 0 : (GridPane.getRowIndex(source));

        //Collects date that matches the 7x7 grid
        Integer curdate = daylayout[rowVal][columnVal];

        //prevents throwing an DateTimeException if the column value = 0 (sunday needs to be 7)
        if (columnVal == 0) {
            columnVal = 7;
        }

        char[] weekArray = DayOfWeek.of(columnVal).toString().toLowerCase().toCharArray();
        weekArray[0] = Character.toUpperCase(weekArray[0]);
        String dayWeek = new String(weekArray);

        StringBuilder output = new StringBuilder();
        output.append(dayWeek).append(", ").append(currentMonth.format(DateTimeFormatter.ofPattern("MMMM"))).append(" ").append(curdate);
        dateLabel.setText(output.toString());
        currentDate = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), curdate);
        Set<Event> dayEvents = getCurrentAccount().getCalendar().getDayEvents(currentDate);
        if (dayEvents.isEmpty()) {
            eventOutput.setDisable(true);
            eventOutput.setText("");
            toggleCompleted.setDisable(true);
            toggleCompleted.setSelected(false);
            updateButton.setDisable(true);
            removeButton.setDisable(true);

        } else {
            //If an event label was pressed we shouldn't overwrite the event it already displayed
            if (!(origSource instanceof Label)) {
                //It doesn't matter what event we show
                currentEvent = (dayEvents.iterator().next());
                printToView();
            }
        }
    }

    /**
     * Views the next event for that given day. Triggered by the right arrow below event details.
     */
    @FXML
    private void viewNextEvent() {
        if (currentDate == null) return;
        SortedSet<Event> dayEvents = getCurrentAccount().getCalendar().getDayEvents(currentDate);

        if (dayEvents.isEmpty()) {
            eventOutput.setDisable(true);
            eventOutput.setText("");
            return;
        }
        if (currentEvent == null || !dayEvents.contains(currentEvent)) {
            currentEvent = dayEvents.first();
            printToView();
            return;
        }
        if (dayEvents.last() == currentEvent) {
            currentEvent = dayEvents.first();
            printToView();
        } else {
            Iterator<Event> it = dayEvents.tailSet(currentEvent).iterator();
            it.next();
            currentEvent = it.next(); //.tailSet() is inclusive, so need second in set
            printToView();
        }
    }

    /**
     * Views the next event for that given day. Triggered by the left arrow below event details.
     */
    @FXML
    private void viewPreviousEvent() {
        if (currentDate == null) return;
        SortedSet<Event> dayEvents = getCurrentAccount().getCalendar().getDayEvents(currentDate);

        if (dayEvents.isEmpty()) {
            eventOutput.setDisable(true);
            eventOutput.setText("");
            return;
        }
        if (currentEvent == null || !dayEvents.contains(currentEvent)) {
            currentEvent = dayEvents.first();
            printToView();
            return;
        }
        if (dayEvents.first() == currentEvent) {
            currentEvent = dayEvents.last();
            printToView();
        } else {
            currentEvent = dayEvents.headSet(currentEvent).last();
            printToView();
        }
    }

    /**
     * Deletes the event from the GUI, and triggers the calendar to remove it from the TreeSet
     */
    @FXML
    private void deleteCurrentEvent() {
        if (currentEvent != null) {
            Event eventToDelete = currentEvent;
            viewNextEvent();
            getCurrentAccount().getCalendar().removeEvent(eventToDelete);
            Account.saveAccounts();
            if (!getCurrentAccount().getCalendar().getDayEvents(currentDate).contains(currentEvent)) {
                currentEvent = null;
            }
            printToView();
        }
    }

    /**
     * Opens the update event GUI page
     */
    @FXML
    private void openUpdatePage() {
        if (currentEvent != null) {
            Stage stage;
            try {
                stage = new Stage();
                java.net.URL resource = getClass().getClassLoader().getResource("UpdateEventGUI.fxml");
                if (resource == null) {
                    resource = getClass().getResource("UpdateEventGUI.fxml");
                }
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root4 = fxmlLoader.load();

                UpdateEventController updateEventController = fxmlLoader.getController();
                updateEventController.mainGUI = this;

                stage.setScene(new Scene(root4, 800, 650));
                stage.setTitle("Update SingularEvent " + currentEvent.getEventName());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(updateButton.getScene().getWindow());
                stage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("You don't have an event selected");
        }

    }

    @FXML
    private void fetchCompleted() {
        Set<Event> completedEvents = getCurrentAccount().getCalendar().getCompletedEvents();
        for (Event currEvent : completedEvents) {
            StringBuilder temp = new StringBuilder();
            temp.append("\n").append(currEvent.getEventName());
            completedEventsArea.setText(temp.toString());
        }
    }

    @FXML
    private void clearCompleted() {
        completedEventsArea.setText("");
    }

    @FXML
    private void submitEvent() {

        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();
        String startingTime = startTimeDropdown.getSelectionModel().getSelectedItem().toString();
        String endingTime = endTimeDropdown.getSelectionModel().getSelectedItem().toString();
        String[] splitStartHM = startingTime.split(":");
        String[] splitEndHM = endingTime.split(":");
        try {
            ZonedDateTime start = ZonedDateTime.of(startDate.getYear(),
                    startDate.getMonthValue(),
                    startDate.getDayOfMonth(),
                    Integer.parseInt(splitStartHM[0]),
                    Integer.parseInt(splitStartHM[1]),
                    0,
                    0,
                    ZoneId.systemDefault());
            ZonedDateTime end = ZonedDateTime.of(endDate.getYear(),
                    endDate.getMonthValue(),
                    endDate.getDayOfMonth(),
                    Integer.parseInt(splitEndHM[0]),
                    Integer.parseInt(splitEndHM[1]),
                    0,
                    0,
                    ZoneId.systemDefault());

            int dateCompare = endDate.compareTo(startDate);
            if (dateCompare < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("This will not do.");
                alert.setHeaderText("Try again, friend.");
                alert.setContentText("Your end date cannot be before your start date!");
                alert.showAndWait();
            } else if (dateCompare == 0 && end.compareTo(start) < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("This will not do.");
                alert.setHeaderText("Try again, friend.");
                alert.setContentText("Your end time cannot be before your start time unless you adjust your dates " + "appropriately");
                alert.showAndWait();
            } else if (eventNameField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("This will not do.");
                alert.setHeaderText("Try again, friend.");
                alert.setContentText("Your event name cannot be blank");
                alert.showAndWait();
            } else {
                SingularEvent event;
                Frequency freq = (Frequency) recurField.getSelectionModel().getSelectedItem();
                switch (freq) {
                    case NEVER:
                        event = new SingularEvent(start, end, eventNameField.getText(), freq);
                        break;
                    default:
                        event = new RecurrentEvent(start, end, eventNameField.getText(), freq, start, end); //Todo fix end
                }
                event.setEventAllDay(allDay.isSelected());
                if (allDay.isSelected()) {
                    ZonedDateTime min = ZonedDateTime.of(startDate.getYear(),
                            startDate.getMonthValue(),
                            startDate.getDayOfMonth(),
                            LocalTime.MIN.getHour(),
                            LocalTime.MIN.getMinute(),
                            0,
                            0,
                            ZoneId.systemDefault());
                    ZonedDateTime max = ZonedDateTime.of(startDate.getYear(),
                            startDate.getMonthValue(),
                            startDate.getDayOfMonth(),
                            LocalTime.MAX.getHour(),
                            LocalTime.MAX.getMinute(),
                            0,
                            0,
                            ZoneId.systemDefault());
                    event.setStart(min);
                    event.setEnd(max);
                }
                event.setEventDesc(eventDescriptionField.getText());
                event.setEventLocation(eventLocationField.getText());
                event.setEventAttendees(eventAttendeesField.getText());
                event.setHighPriority(highPrior.isSelected());

                getCurrentAccount().getCalendar().addEvent(event);
                Account.saveAccounts();

                viewMonth(currentMonth);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Event was created!");
                alert.setHeaderText("Well done");
                alert.setContentText("Your event has been added to the calendar");
                alert.showAndWait();
                SingleSelectionModel<Tab> selector = tabPane.getSelectionModel();
                clearEvent();
                selector.selectFirst();
            }
        } catch (Exception e) { //Unable to parse start/end times
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable to Parse Time(s)");
            alert.setHeaderText("Unable to Parse Start/End Times");
            alert.setContentText("Please ensure that your times are valid.");
            alert.showAndWait();
        }
    }

    private void clearEvent() {
        eventNameField.setText("");
        eventDescriptionField.setText("");
        eventLocationField.setText("");
        eventAttendeesField.setText("");
        highPrior.setSelected(false);
        allDay.setSelected(false);
        startDateField.setValue(LocalDate.now());
        endDateField.setValue(LocalDate.now());
        startTimeDropdown.getSelectionModel().selectFirst();
        endTimeDropdown.getSelectionModel().selectFirst();
    }

    @FXML
    private void adjustEndDate() {
        LocalDate startDate = startDateField.getValue();
        long days = DAYS.between(oldDateValue, startDate);

        endDateField.setValue(endDateField.getValue().plusDays(days));
        oldDateValue = startDate;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public ZonedDateTime getCurrentMonth() {
        return currentMonth;
    }
}
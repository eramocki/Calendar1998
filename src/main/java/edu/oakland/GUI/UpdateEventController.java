package edu.oakland.GUI;

import edu.oakland.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class UpdateEventController {

    private static final Logger logger = Logger.getLogger(UpdateEventController.class.getName());

    public MainGUI mainGUI;

    @FXML
    private DatePicker startDateField, endDateField, recurrenceEndDate;

    @FXML
    private ComboBox startTimeDropdown, endTimeDropdown, recurField;

    @FXML
    private TextField eventNameField, eventLocationField, eventAttendeesField;

    @FXML
    private TextArea eventDescriptionField;

    @FXML
    private CheckBox allDay, highPrior;

    @FXML
    public void initialize() {
        GUIHelper.setupTimeCombobox(startTimeDropdown, LocalTime.MIDNIGHT);
        GUIHelper.setupTimeCombobox(endTimeDropdown, LocalTime.MIDNIGHT.plusSeconds(1));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                LocalTime startTime = mainGUI.getCurrentEvent().getStart().toLocalTime();
                LocalTime endTime = mainGUI.getCurrentEvent().getEnd().toLocalTime();

                startTimeDropdown.setValue(startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                endTimeDropdown.setValue(endTime.format(DateTimeFormatter.ofPattern("HH:mm")));

                startDateField.setValue(mainGUI.getCurrentEvent().getStart().toLocalDate());
                endDateField.setValue(mainGUI.getCurrentEvent().getEnd().toLocalDate());

                recurField.getItems().clear();
                for (Frequency freq : Frequency.values()) {
                    recurField.getItems().add(freq);
                }
                recurField.getSelectionModel().selectFirst();
                
                eventNameField.setText(mainGUI.getCurrentEvent().getEventName());
                eventDescriptionField.setText(mainGUI.getCurrentEvent().getEventDesc());
                eventLocationField.setText(mainGUI.getCurrentEvent().getEventLocation());
                eventAttendeesField.setText(mainGUI.getCurrentEvent().getEventAttendees());
                if (mainGUI.getCurrentEvent().getEventAllDay()) {
                    allDay.setSelected(true);
                }
                if (mainGUI.getCurrentEvent().getHighPriority()) {
                    highPrior.setSelected(true);
                }
            }
        });

    }

    @FXML
    private void modifyEvent(ActionEvent actionEvent) {
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
                alert.setContentText("Your end time cannot be before your start time unless you adjust your dates "
                        + "appropriately");
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
                        LocalDate d = recurrenceEndDate.getValue();
                        ZonedDateTime recurEnd = ZonedDateTime.of(d, end.toLocalTime(), end.getZone());
                        event = new RecurrentEvent(start, end, eventNameField.getText(), freq, start, recurEnd); //Todo fix end
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

                Event selectedEvent = mainGUI.getCurrentEvent();
                mainGUI.getCurrentAccount().getCalendar().updateEvent(selectedEvent, event);

                Account.saveAccounts();

                mainGUI.viewMonth(mainGUI.getCurrentMonth());
                mainGUI.setCurrentEvent(event);
                mainGUI.printToView();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Event was created!");
                alert.setHeaderText("Well done");
                alert.setContentText("Your event has been added to the calendar");
                alert.showAndWait();

                ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            }
        } catch (Exception e) { //Unable to parse start/end times
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable to Parse Time(s)");
            alert.setHeaderText("Unable to Parse Start/End Times");
            alert.setContentText("Please ensure that your times are valid and different from the original times.");
            alert.showAndWait();
        }
    }
}

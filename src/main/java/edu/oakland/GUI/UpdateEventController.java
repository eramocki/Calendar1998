package edu.oakland.GUI;

import edu.oakland.Account;
import edu.oakland.SingularEvent;
import edu.oakland.Frequency;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

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
    private DatePicker startDateField, endDateField;

    @FXML
    private ComboBox startTimeDropdown, endTimeDropdown, recurField;

    @FXML
    private TextField eventNameField, eventDescriptionField, eventLocationField, eventAttendeesField;

    @FXML
    private CheckBox allDay, highPrior;

    @FXML
    public void initialize() {
        GUIHelper.setupTimeCombobox(startTimeDropdown, LocalTime.MIDNIGHT);
        GUIHelper.setupTimeCombobox(endTimeDropdown, LocalTime.MIDNIGHT.plusSeconds(1));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                ZonedDateTime st = mainGUI.getCurrentEvent().getStart();
                ZonedDateTime end = mainGUI.getCurrentEvent().getEnd();
                StringBuilder startTimeBuilder = new StringBuilder();
                StringBuilder endTimeBuilder = new StringBuilder();
                if (mainGUI.getCurrentEvent().getEventAllDay()) {
                    startTimeBuilder.append("\n00:00");
                    endTimeBuilder.append("\n00:00");
                } else {
                    startTimeBuilder.append(st.toLocalDateTime().format(DateTimeFormatter.ofPattern("\nHH:mm")));
                    endTimeBuilder.append(end.toLocalDateTime().format(DateTimeFormatter.ofPattern("\nHH:mm")));
                }
                startTimeDropdown.setValue(startTimeBuilder.toString());
                endTimeDropdown.setValue(endTimeBuilder.toString());
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
    private void modifyEvent(ActionEvent event) {
        LocalDate startDateUpdate = startDateField.getValue();
        LocalDate endDateUpdate = endDateField.getValue();
        String startingTimeUpdate = startTimeDropdown.getSelectionModel().getSelectedItem().toString();
        String endingTimeUpdate = endTimeDropdown.getSelectionModel().getSelectedItem().toString();

        String[] splitStartHM = startingTimeUpdate.split(":");
        String[] splitEndHM = endingTimeUpdate.split(":");

        ZonedDateTime start = ZonedDateTime.of(startDateUpdate.getYear(), startDateUpdate.getMonthValue(), startDateUpdate.getDayOfMonth(), Integer.parseInt(splitStartHM[0]), Integer.parseInt(splitStartHM[1]), 0, 0, ZoneId.systemDefault());
        ZonedDateTime end = ZonedDateTime.of(endDateUpdate.getYear(), endDateUpdate.getMonthValue(), endDateUpdate.getDayOfMonth(), Integer.parseInt(splitEndHM[0]), Integer.parseInt(splitEndHM[1]), 0, 0, ZoneId.systemDefault());

        if (end.compareTo(start) < 0 || endingTimeUpdate.compareTo(startingTimeUpdate) < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("You can't have your end date/time happen in the past!");

            alert.showAndWait();
        } else if (eventNameField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("This will not do.");
            alert.setHeaderText("Try again, friend.");
            alert.setContentText("Your event name cannot be blank");

            alert.showAndWait();
        } else {
            SingularEvent updateEvent = new SingularEvent(start, end, eventNameField.getText());
            updateEvent.setEventDesc(eventDescriptionField.getText());
            updateEvent.setEventLocation(eventAttendeesField.getText());
            updateEvent.setEventAttendees(eventAttendeesField.getText());
            updateEvent.setEventAllDay(allDay.isSelected());
            updateEvent.setHighPriority(highPrior.isSelected());
            updateEvent.setFrequency(Frequency.valueOf(recurField.getSelectionModel().getSelectedItem().toString().toUpperCase()));
            mainGUI.getCurrentAccount().getCalendar().addEvent(updateEvent);
            mainGUI.viewMonth(mainGUI.getCurrentMonth());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SingularEvent was updated!.");
            alert.setHeaderText("Well done");
            alert.setContentText("Your event has been update on the calendar");
            alert.showAndWait();
            Account.saveAccounts();
        }
    }
}

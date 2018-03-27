package edu.oakland.GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Functions that are useful for any GUI to have
 */
public class GUIHelper {

    /**
     * Setup a combobox with times every half hour from midnight to end of day
     *
     * @param theComboBox the ComboBox to setup
     * @param selected    the time that will be selected
     */
    public static void setupTimeCombobox(ComboBox theComboBox, LocalTime selected) {
        setupTimeCombobox(theComboBox, selected, LocalTime.MIDNIGHT, Duration.ofMinutes(30));
    }

    /**
     * Setup a combobox with times from midnight to end of day, in terms of the given increment
     *
     * @param theComboBox the ComboBox to setup
     * @param selected    the time that will be selected
     * @param increment   the time between each item
     */
    public static void setupTimeCombobox(ComboBox theComboBox, LocalTime selected, Duration increment) {
        setupTimeCombobox(theComboBox, selected, LocalTime.MIDNIGHT, increment);
    }

    /**
     * Setup a combobox with times every half hour from midnight to end of day,
     * where the minutes are relative to the given time
     * <p>
     * e.g. relativeTo of 9:43 --> 0:13, 0:43, 1:13 ...
     *
     * @param theComboBox the ComboBox to setup
     * @param selected    the time that will be selected
     * @param relativeTo  the time that the increments are relative to
     */
    public static void setupTimeCombobox(ComboBox theComboBox, LocalTime selected, LocalTime relativeTo) {
        setupTimeCombobox(theComboBox, selected, relativeTo, Duration.ofMinutes(30));
    }

    /**
     * Setup a combobox with times every increment from midnight to end of day,
     * where the minutes are relative to the given time
     * <p>
     * e.g. relativeTo of 9:43; increment of 30 min --> 0:13, 0:43, 1:13 ...
     *
     * @param theComboBox the ComboBox to setup
     * @param selected    the time that will be selected
     * @param relativeTo  the time that the increments are relative to
     * @param increment   the time between each item
     */
    public static void setupTimeCombobox(ComboBox theComboBox, LocalTime selected, LocalTime relativeTo, Duration increment) {
        theComboBox.getItems().clear();

        LocalTime current = relativeTo;
        while (current.isAfter(current.minus(increment))) {
            current = current.minus(increment);
        }
        theComboBox.getItems().add(current.format(DateTimeFormatter.ofPattern("HH:mm")));

        while (current.isBefore(current.plus(increment))) {
            theComboBox.getItems().add(current.plus(increment).format(DateTimeFormatter.ofPattern("HH:mm")));
            current = current.plus(increment);
        }

        theComboBox.setValue(selected.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    /**
     * Create an error alert (dialog box) on screen.
     *
     * @param title   the Title of the dialog
     * @param header  the string to show in the dialog header area
     * @param content the string to show in the dialog content area
     */
    public static void errorAlert(String title, String header, String content) {
        alert(title, header, content, Alert.AlertType.ERROR);
    }

    /**
     * Create an alert (dialog box) on screen.
     *
     * @param title   the Title of the dialog
     * @param header  the string to show in the dialog header area
     * @param content the string to show in the dialog content area
     * @param type    the AlertType
     */
    public static void alert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

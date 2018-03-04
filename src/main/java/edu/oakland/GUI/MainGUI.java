package edu.oakland.GUI;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.GridPane;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class MainGUI {

    @FXML
    private GridPane calendarGridPane;

    @FXML
    public void initialize() {

        //Create labels for day of week header
        int columnIndex = 0; //Which column to put the next label in
        for (int i = 6; i < 13; i++) { //Each day of the week by number, want to start at sunday so numbering is offset
            DayOfWeek dayOfWeek = DayOfWeek.of((i % 7) + 1); //Start at sunday, which is day 7
            Label DoWLabel = new Label();
            DoWLabel.setAlignment(Pos.BOTTOM_CENTER);
            DoWLabel.setMaxWidth(Double.MAX_VALUE); //So the labels fill the grid they are in
            DoWLabel.setMaxHeight(Double.MAX_VALUE);
            DoWLabel.setText(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US));
            calendarGridPane.add(DoWLabel, columnIndex++, 0);
        }

    }

}

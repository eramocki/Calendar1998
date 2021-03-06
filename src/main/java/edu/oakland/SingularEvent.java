package edu.oakland;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Random;

public class SingularEvent implements Serializable, Event {

    protected ZonedDateTime start, end;
    protected String eventName, eventDesc, eventLocation, eventAttendees;
    protected boolean eventAllDay, isHighPriority, isCompleted;
    protected Frequency frequency;

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public SingularEvent(Event e) {
        start = e.getStart();
        end = e.getEnd();
        eventName = e.getEventName();
        eventDesc = e.getEventDesc();
        eventLocation = e.getEventLocation();
        eventAttendees = e.getEventAttendees();
        eventAllDay = e.getEventAllDay();
        isHighPriority = e.getHighPriority();
        isCompleted = e.getCompleted();
        frequency = e.getFrequency();
    }

    public SingularEvent(ZonedDateTime startDateTime, ZonedDateTime endDateTime, String eventName) {
        this(startDateTime, endDateTime, eventName, Frequency.NEVER);

    }

    public SingularEvent(ZonedDateTime startDateTime, ZonedDateTime endDateTime, String eventName, Frequency frequency) {
        start = startDateTime;
        end = endDateTime;
        this.eventName = eventName;
        this.frequency = frequency;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventAttendees() {
        return eventAttendees;
    }

    public void setEventAttendees(String eventAttendees) {
        this.eventAttendees = eventAttendees;
    }

    public Boolean getEventAllDay() {
        return eventAllDay;
    }

    public void setEventAllDay(Boolean eventAllDay) {
        this.eventAllDay = eventAllDay;
    }

    public Boolean getHighPriority() {
        return isHighPriority;
    }

    public void setHighPriority(Boolean highPriority) {
        isHighPriority = highPriority;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public boolean getCompleted() { return isCompleted; }

    public void setCompleted(boolean completed) { isCompleted = completed; }
}

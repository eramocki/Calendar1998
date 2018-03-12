package edu.oakland;

import java.time.LocalDate;

public class Event {

    private int startTime, stopTime;
    private LocalDate startDate, stopDate;
    private String eventName, eventDesc, eventLocation, eventAttendees;
    private Boolean eventAllDay, isHighPriority;
    private Frequency frequency;

    /**
     * Minimal input required for Event generation
     */
    public Event(int startTime, int stopTime, LocalDate startDate, LocalDate stopDate, String eventName)
    {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.eventName = eventName;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getStopTime() {
        return stopTime;
    }

    public void setStopTime(int stopTime) {
        this.stopTime = stopTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStopDate() {
        return stopDate;
    }

    public void setStopDate(LocalDate stopDate) {
        this.stopDate = stopDate;
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
}

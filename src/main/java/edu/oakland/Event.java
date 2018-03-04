package edu.oakland;

import java.util.Date;

public class Event {

    private int startTime, stopTime;
    private Date startDate, stopDate;
    private String eventName, eventDesc, eventLocation, eventAttendees;
    private Boolean eventAllDay, eventHappensOnce, eventWeekly, eventMonthly, isHighPriority;

    /**
     * Minimal input required for Event generation
     */
    public Event(int startTime, int stopTime, Date startDate, Date stopDate, String eventName)
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
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

    public Boolean getEventHappensOnce() {
        return eventHappensOnce;
    }

    public void setEventHappensOnce(Boolean eventHappensOnce) {
        this.eventHappensOnce = eventHappensOnce;
    }

    public Boolean getEventWeekly() {
        return eventWeekly;
    }

    public void setEventWeekly(Boolean eventWeekly) {
        this.eventWeekly = eventWeekly;
    }

    public Boolean getEventMonthly() {
        return eventMonthly;
    }

    public void setEventMonthly(Boolean eventMonthly) {
        this.eventMonthly = eventMonthly;
    }

    public Boolean getHighPriority() {
        return isHighPriority;
    }

    public void setHighPriority(Boolean highPriority) {
        isHighPriority = highPriority;
    }
}

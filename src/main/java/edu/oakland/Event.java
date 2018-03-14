package edu.oakland;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Event implements Serializable {

    private ZonedDateTime start, end;
    private String eventName, eventDesc, eventLocation, eventAttendees;
    private Boolean eventAllDay, isHighPriority;
    private Frequency frequency;

    public Event(ZonedDateTime startDateTime, ZonedDateTime endDateTime, String eventName) {
        this(startDateTime, endDateTime, eventName, Frequency.ONCE);
    }

    public Event(ZonedDateTime startDateTime, ZonedDateTime endDateTime, String eventName, Frequency frequency) {
        start = startDateTime;
        end = endDateTime;
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
}

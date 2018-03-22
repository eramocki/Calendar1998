package edu.oakland;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Event implements Serializable {

    protected ZonedDateTime start, end;
    protected ZonedDateTime recurrenceBegin, recurrenceEnd;
    protected String eventName, eventDesc, eventLocation, eventAttendees;
    protected boolean eventAllDay, isHighPriority, isCompleted;
    protected Frequency frequency;

    public Event(ZonedDateTime startDateTime, ZonedDateTime endDateTime, String eventName) {
        this(startDateTime, endDateTime, eventName, Frequency.NEVER);
    }

    public Event(ZonedDateTime startDateTime, ZonedDateTime endDateTime, String eventName, Frequency frequency) {
        start = startDateTime;
        end = endDateTime;
        this.eventName = eventName;
        this.frequency = frequency;
    }

    /**
     * Determine if this event happens or is happening at some time on the specified day.
     * <p>
     * e.g.
     * The event is one hour on the given day
     * The event spans multiple days around the given day
     * The event starts on this day and continues for days
     * etc.
     *
     * @param date the date to check
     * @return True if this event happens on the date
     */
    public boolean happensSometimeOnDate(LocalDate date) {
        return start.isBefore(ZonedDateTime.of(date, LocalTime.MAX, ZoneId.systemDefault())) && end.isAfter(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault()));
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

    public ZonedDateTime getRecurrenceBegin() {
        return recurrenceBegin;
    }

    public void setRecurrenceBegin(ZonedDateTime recurrenceBegin) {
        this.recurrenceBegin = recurrenceBegin;
    }

    public ZonedDateTime getRecurrenceEnd() {
        return recurrenceEnd;
    }

    public void setRecurrenceEnd(ZonedDateTime recurrenceEnd) {
        this.recurrenceEnd = recurrenceEnd;
    }

    public Boolean getCompleted() { return isCompleted; }

    public void setCompleted(Boolean completed) { isCompleted = completed; }
}

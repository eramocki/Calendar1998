package edu.oakland;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface Event {

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
    default public boolean happensOnDate(LocalDate date) {
        return getStart().isBefore(ZonedDateTime.of(date, LocalTime.MAX, ZoneId.systemDefault())) &&
                getEnd().isAfter(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault()));
    }

    public String getEventName();

    public void setEventName(String eventName);

    public String getEventDesc();

    public void setEventDesc(String eventDesc);

    public String getEventLocation();

    public void setEventLocation(String eventLocation);

    public String getEventAttendees();

    public void setEventAttendees(String eventAttendees);

    public Boolean getEventAllDay();

    public void setEventAllDay(Boolean eventAllDay);

    public Boolean getHighPriority();

    public void setHighPriority(Boolean highPriority);

    public Frequency getFrequency();

    public void setFrequency(Frequency frequency);

    public ZonedDateTime getStart();

    public void setStart(ZonedDateTime start);

    public ZonedDateTime getEnd();

    public void setEnd(ZonedDateTime end);

    public boolean getCompleted();

    public void setCompleted(boolean completed);
}

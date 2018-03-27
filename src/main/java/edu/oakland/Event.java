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
    default boolean happensOnDate(LocalDate date) {
        return getStart().isBefore(ZonedDateTime.of(date.plusDays(1), LocalTime.MIN, ZoneId.systemDefault())) &&
                getEnd().isAfter(ZonedDateTime.of(date.minusDays(1), LocalTime.MAX, ZoneId.systemDefault()));
    }

    String getEventName();

    void setEventName(String eventName);

    String getEventDesc();

    void setEventDesc(String eventDesc);

    String getEventLocation();

    void setEventLocation(String eventLocation);

    String getEventAttendees();

    void setEventAttendees(String eventAttendees);

    Boolean getEventAllDay();

    void setEventAllDay(Boolean eventAllDay);

    Boolean getHighPriority();

    void setHighPriority(Boolean highPriority);

    Frequency getFrequency();

    void setFrequency(Frequency frequency);

    ZonedDateTime getStart();

    void setStart(ZonedDateTime start);

    ZonedDateTime getEnd();

    void setEnd(ZonedDateTime end);

    boolean getCompleted();

    void setCompleted(boolean completed);
}

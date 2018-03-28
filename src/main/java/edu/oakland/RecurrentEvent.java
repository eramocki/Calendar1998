package edu.oakland;

import java.time.ZonedDateTime;

public class RecurrentEvent extends SingularEvent {

    private ZonedDateTime recurrenceBegin, recurrenceEnd;

    public RecurrentEvent(ZonedDateTime startDateTime, ZonedDateTime endDateTime, String eventName, Frequency frequency, ZonedDateTime recurrenceBegin, ZonedDateTime recurrenceEnd) {
        super(startDateTime, endDateTime, eventName, frequency);
        this.recurrenceBegin = recurrenceBegin;
        this.recurrenceEnd = recurrenceEnd;
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
}

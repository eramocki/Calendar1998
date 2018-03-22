package edu.oakland;

import java.time.ZonedDateTime;

public class EphemeralEvent extends Event {

    protected final Event parent;

    protected EphemeralEvent(ZonedDateTime startDateTime, ZonedDateTime endDateTime, String eventName, Frequency frequency, Event parent) {
        super(startDateTime, endDateTime, eventName, frequency);
        this.parent = parent;
    }
}

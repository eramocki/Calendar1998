package edu.oakland;

import java.time.ZonedDateTime;

public class EphemeralEvent extends Event {

    protected final Event parent;

    protected EphemeralEvent(ZonedDateTime startDateTime, ZonedDateTime endDateTime, Event parent) {
        super(startDateTime, endDateTime, parent.eventName);
        this.parent = parent;
    }
}

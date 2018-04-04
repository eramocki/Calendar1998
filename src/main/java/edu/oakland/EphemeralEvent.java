package edu.oakland;

import javafx.scene.layout.Background;

import java.time.ZonedDateTime;

public class EphemeralEvent implements Event {

    private RecurrentEvent parent;
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;

    EphemeralEvent(ZonedDateTime startDateTime, ZonedDateTime endDateTime, RecurrentEvent parent) {
        this.parent = parent;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public RecurrentEvent getParent() {
        return parent;
    }

    @Override
    public String getEventName() {
        return parent.getEventName();
    }

    @Override
    public void setEventName(String eventName) {
        parent.setEventName(eventName);
    }

    @Override
    public String getEventDesc() {
        return parent.getEventDesc();
    }

    @Override
    public void setEventDesc(String eventDesc) {
        parent.setEventDesc(eventDesc);
    }

    @Override
    public String getEventLocation() {
        return parent.getEventLocation();
    }

    @Override
    public void setEventLocation(String eventLocation) {
        parent.setEventLocation(eventLocation);
    }

    @Override
    public String getEventAttendees() {
        return parent.getEventAttendees();
    }

    @Override
    public void setEventAttendees(String eventAttendees) {
        parent.setEventAttendees(eventAttendees);
    }

    @Override
    public Boolean getEventAllDay() {
        return parent.getEventAllDay();
    }

    @Override
    public void setEventAllDay(Boolean eventAllDay) {
        parent.setEventAllDay(eventAllDay);
    }

    @Override
    public Boolean getHighPriority() {
        return parent.getHighPriority();
    }

    @Override
    public void setHighPriority(Boolean highPriority) {
        parent.setHighPriority(highPriority);
    }

    @Override
    public Frequency getFrequency() {
        return parent.getFrequency();
    }

    @Override
    public void setFrequency(Frequency frequency) {
        throw new UnsupportedOperationException("Not yet implemented"); //Todo figure this out
    }

    @Override
    public ZonedDateTime getStart() {
        return startDateTime;
    }

    @Override
    public void setStart(ZonedDateTime start) {
        throw new UnsupportedOperationException("Not yet implemented"); //Todo figure this out
    }

    @Override
    public ZonedDateTime getEnd() {
        return endDateTime;
    }

    @Override
    public void setEnd(ZonedDateTime end) {
        throw new UnsupportedOperationException("Not yet implemented"); //Todo figure this out
    }

    @Override
    public boolean getCompleted() {
        return parent.getCompleted();
    }

    @Override
    public void setCompleted(boolean completed) {
        parent.setCompleted(completed);
    }
}

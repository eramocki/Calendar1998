package edu.oakland;

import javafx.scene.layout.Background;

import java.time.ZonedDateTime;

public class EphemeralEvent implements Event {

    private RecurrentEvent e;
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;

    EphemeralEvent(ZonedDateTime startDateTime, ZonedDateTime endDateTime, RecurrentEvent parent) {
        e = parent;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    @Override
    public String getEventName() {
        return e.getEventName();
    }

    @Override
    public void setEventName(String eventName) {
        e.setEventName(eventName);
    }

    @Override
    public String getEventDesc() {
        return e.getEventDesc();
    }

    @Override
    public void setEventDesc(String eventDesc) {
        e.setEventDesc(eventDesc);
    }

    @Override
    public String getEventLocation() {
        return e.getEventLocation();
    }

    @Override
    public void setEventLocation(String eventLocation) {
        e.setEventLocation(eventLocation);
    }

    @Override
    public String getEventAttendees() {
        return e.getEventAttendees();
    }

    @Override
    public void setEventAttendees(String eventAttendees) {
        e.setEventAttendees(eventAttendees);
    }

    @Override
    public Boolean getEventAllDay() {
        return e.getEventAllDay();
    }

    @Override
    public void setEventAllDay(Boolean eventAllDay) {
        e.setEventAllDay(eventAllDay);
    }

    @Override
    public Boolean getHighPriority() {
        return e.getHighPriority();
    }

    @Override
    public void setHighPriority(Boolean highPriority) {
        e.setHighPriority(highPriority);
    }

    @Override
    public Frequency getFrequency() {
        return e.getFrequency();
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
        return e.getCompleted();
    }

    @Override
    public void setCompleted(boolean completed) {
        e.setCompleted(completed);
    }

    @Override
    public Background getBackground(){return e.bg;}
}

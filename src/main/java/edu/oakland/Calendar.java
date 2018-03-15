package edu.oakland;

import java.io.Serializable;
import java.time.*;
import java.util.*;

public class Calendar implements Serializable {

    public TreeSet<Event> startingSet;
    public TreeSet<Event> endingSet;

    public Calendar() {
        startingSet = new TreeSet<>(StartComparator.INSTANCE);
        endingSet = new TreeSet<>(EndComparator.INSTANCE);
    }

    public Set<Event> getMonthEvents(YearMonth yearMonth) {
        ZonedDateTime start = ZonedDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1, 0, 0,
                0, 0, ZoneId.systemDefault());
        ZonedDateTime end = ZonedDateTime.of(yearMonth.atEndOfMonth(), LocalTime.MAX, ZoneId.systemDefault());
        Event earliest = new Event(start, start, "");
        Event latest = new Event(end, end, "");
        Set<Event> union = new HashSet<>();
        union.addAll(endingSet.subSet(earliest, latest));
        union.addAll(startingSet.subSet(earliest, latest));
        return union;
    }

    public Set<Event> getDayEvents(LocalDate localDate){
        ZonedDateTime start = localDate.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = localDate.atStartOfDay(ZoneId.systemDefault()).plusMinutes(1439);
        Event earliest = new Event(start, start, "");
        Event latest = new Event(end, end, "");
        Set<Event> union = new HashSet<>();
        union.addAll(endingSet.subSet(earliest, latest));
        union.addAll(startingSet.subSet(earliest, latest));
        return union;
    }

    //LocalDate myDate
    public void viewEvent() {
        //iterate through event treeset
        //if startdate == myDate
            //draw event information on calendar's right panel
        System.out.println("works");
    }

    public void displayEvent(LocalDate myDate){
        //draw to grid on calendar setup

        //if event exists on this day
            //draw to calendar grid
    }

    public void listEvent(){} //TODO

    public void addEvent(Event event){
        startingSet.add(event);
        endingSet.add(event);
    }

    public void removeEvent(Event event){}
    public void updateEvent(Event event){}

    //You can ignore these enums. They're a workaround to serialize lambdas.
    private enum StartComparator implements Comparator<Event> {
        INSTANCE;
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getStart().compareTo(o2.getStart());
        }
    }

    private enum EndComparator implements Comparator<Event> {
        INSTANCE;
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getEnd().compareTo(o2.getEnd());
        }
    }

}

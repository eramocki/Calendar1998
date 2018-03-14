package edu.oakland;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.TreeSet;

public class Calendar implements Serializable {

    public TreeSet<Event> startingSet;
    public TreeSet<Event> endingSet;

    public Calendar() {
        startingSet = new TreeSet<>(StartComparator.INSTANCE);
        endingSet = new TreeSet<>(EndComparator.INSTANCE);
    }

    public void viewEvent(LocalDate myDate) {
        //iterate through event treeset
        //if startdate == myDate
            //draw event information on calendar's right panel
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

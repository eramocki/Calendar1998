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

    public void displayEvent(LocalDate myDate) {
        //iterate through event treeset
        //if startdate == myDate
            //displayEvent to GUI
        //else do nothing
    }

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
            return o1.getStartDate().compareTo(o2.getStartDate());
        }
    }

    private enum EndComparator implements Comparator<Event> {
        INSTANCE;
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getStopDate().compareTo(o2.getStopDate());
        }
    }

}

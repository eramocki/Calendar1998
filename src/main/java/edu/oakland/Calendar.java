package edu.oakland;

import java.io.Serializable;
import java.time.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Calendar implements Serializable {

    private transient static final Logger logger = Logger.getLogger(Calendar.class.getName());

    public TreeSet<Event> startingSet;
    public TreeSet<Event> endingSet;

    public Calendar() {
        startingSet = new TreeSet<>(StartComparator.INSTANCE);
        endingSet = new TreeSet<>(EndComparator.INSTANCE);
    }

    /**
     *
     * @param yearMonth
     * @return
     */
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

    /**
     *
     * @param localDate
     * @return
     */
    public SortedSet<Event> getDayEvents(LocalDate localDate) {
        TreeSet<Event> intersection = new TreeSet<>(StartComparator.INSTANCE);

        Instant minInstant = Instant.ofEpochMilli(Long.MIN_VALUE);
        Instant maxInstant = Instant.ofEpochMilli(Long.MAX_VALUE);
        ZonedDateTime minZonedDateTime = minInstant.atZone(ZoneOffset.UTC);
        ZonedDateTime maxZonedDateTime = maxInstant.atZone(ZoneOffset.UTC);

        Event bound1 = new Event(minZonedDateTime, localDate.atStartOfDay(ZoneId.systemDefault()), "");

        Event bound2 = new Event(ZonedDateTime.of(localDate.atTime(23, 59, 59), ZoneId.systemDefault()), maxZonedDateTime, "");

        intersection.addAll(startingSet.subSet(bound1, bound2));
        intersection.retainAll(endingSet.subSet(bound1, bound2));

        // Do NOT change the formatting on this
        // :)
        Set<Event> recurringEvents = startingSet.stream()
                .filter(e -> e.frequency != Frequency.NEVER)
                .filter(e -> e.recurrenceBegin.isBefore(localDate.atStartOfDay(ZoneId.systemDefault())))
                .filter(e -> !e.recurrenceEnd.isBefore(localDate.atStartOfDay(ZoneId.systemDefault())))
                .collect(Collectors.toSet());

        TreeSet<Event> ephemeralEvents = new TreeSet<>(StartComparator.INSTANCE);

        for (Event recurringEvent : recurringEvents) {
            ZonedDateTime startOfEvent = recurringEvent.getStart();
            ZonedDateTime endOfEvent = recurringEvent.getEnd();

            whileBeforeRecurrenceEnd:
            while (endOfEvent.isBefore(recurringEvent.recurrenceEnd) ) { //&& endOfEvent.isBefore(localDate.atStartOfDay(ZoneId.systemDefault()).plusHours(23))
                EphemeralEvent ee = new EphemeralEvent(startOfEvent, endOfEvent, recurringEvent);
                switch (recurringEvent.frequency) {
                    case DAILY:
                        startOfEvent = startOfEvent.plusDays(1);
                        endOfEvent = endOfEvent.plusDays(1);
                        break;
                    case WEEKLY:
                        startOfEvent = startOfEvent.plusWeeks(1);
                        endOfEvent = endOfEvent.plusWeeks(1);
                        break;
                    case MONTHLY:
                        startOfEvent = startOfEvent.plusMonths(1);
                        endOfEvent = endOfEvent.plusMonths(1);
                        break;
                    case NEVER:
                    default:
                        logger.info("Something spookys' going on!"); //Should not happen
                        break whileBeforeRecurrenceEnd;
                }
                ephemeralEvents.add(ee);
            }
        }

        Set<Event> ephemeralEventsWithinDay = ephemeralEvents.stream()
                .filter(e -> e.happensSometimeOnDate(localDate))
                .collect(Collectors.toSet());

        intersection.addAll(ephemeralEventsWithinDay);

        return intersection;
    }

    /**
     *
     * @return
     */
    public SortedSet<Event> getCompletedEvents(){
        TreeSet<Event> completedSet = new TreeSet<>(StartComparator.INSTANCE);
        Iterator<Event> ir = startingSet.iterator();
        while (ir.hasNext()) {
            Event currEvent = ir.next();
            if (currEvent.getCompleted()) {
                completedSet.add(currEvent);
            }
        }
        return completedSet;
    }


    /**
     *
     * @param event
     */
    public void addEvent(Event event){
        startingSet.add(event);
        endingSet.add(event);
    }

    /**
     *
     * @param event
     */
    public void removeEvent(Event event){
        startingSet.remove(event);
        endingSet.remove(event);
    }

    /**
     *
     * @param oldEvent
     * @param newEvent
     */
    public void updateEvent(Event oldEvent, Event newEvent){
        startingSet.remove(oldEvent);
        startingSet.add(newEvent);
        endingSet.remove(oldEvent);
        endingSet.add(newEvent);
    }

    //You can ignore these enums. They're a workaround to serialize lambdas.

    /**
     *
     */
    private enum StartComparator implements Comparator<Event> {
        INSTANCE;
        @Override
        public int compare(Event o1, Event o2) {
            int c = o1.getStart().compareTo(o2.getStart());
            if (c != 0) {
                return c;
            } else {
                return o1.getEventName().compareToIgnoreCase(o2.getEventName());
            }
        }
    }

    /**
     *
     */
    private enum EndComparator implements Comparator<Event> {
        INSTANCE;
        @Override
        public int compare(Event o1, Event o2) {
            int c = o1.getEnd().compareTo(o2.getEnd());
            if (c != 0) {
                return c;
            } else {
                return o1.getEventName().compareToIgnoreCase(o2.getEventName());
            }
        }
    }

}

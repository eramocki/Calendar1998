package edu.oakland;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Calendar implements Serializable {

    private transient static final Logger logger = Logger.getLogger(Calendar.class.getName());

    public transient HashMap<YearMonth, Set<Event>> monthCache;

    public TreeSet<Event> startingSet;
    public TreeSet<Event> endingSet;
    private TreeSet<RecurrentEvent> recurringEndingSet;

    public Calendar() {
        startingSet = new TreeSet<>(StartComparator.INSTANCE);
        endingSet = new TreeSet<>(EndComparator.INSTANCE);
        recurringEndingSet = new TreeSet<>(RecurrenceEndComparator.INSTANCE);
        monthCache = new HashMap<>();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        monthCache = new HashMap<>();
    }

    /**
     * Either computes the month's events and caches it or merely pulls the month from the cache if it is found within
     * @param yearMonth The month to be examined
     * @return The Set of events occurring in that month
     */
    public Set<Event> getMonthEvents(YearMonth yearMonth) {
        return monthCache.computeIfAbsent(yearMonth, this::computeMonthEvents);
    }

    private Set<Event> computeMonthEvents(YearMonth yearMonth) {
        ZonedDateTime starting = ZonedDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1, 0, 0,
                0, 0, ZoneId.systemDefault());
        ZonedDateTime ending = ZonedDateTime.of(yearMonth.atEndOfMonth(), LocalTime.MAX, ZoneId.systemDefault());
        RecurrentEvent earliest = new RecurrentEvent(starting, starting, "", Frequency.NEVER, starting, starting);
        Set<Event> result = new HashSet<>();

        recurringEndingSet.tailSet(earliest).stream().filter(e -> e.getRecurrenceBegin().isBefore(ending))
                .forEach(r -> result.addAll(spawnEphemeralEvents(r, yearMonth)));
        result.addAll(endingSet.tailSet(earliest).stream().filter(e -> e.getStart().isBefore(ending))
                .collect(Collectors.toSet()));
        return result;
    }

    private Set<Event> spawnEphemeralEvents(RecurrentEvent recurrentEvent, YearMonth yearMonth) {
        ZonedDateTime startOfEvent = recurrentEvent.getStart();
        ZonedDateTime endOfEvent = recurrentEvent.getEnd();
        ZonedDateTime endOfRecurrence = recurrentEvent.getRecurrenceEnd().plusDays(1);
        Set<Event> ephemeralEvents = new HashSet<>();
        ZonedDateTime bound = yearMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault());
        while (endOfEvent.isBefore(endOfRecurrence) && endOfEvent.isBefore(bound)) {
            EphemeralEvent ee = new EphemeralEvent(startOfEvent, endOfEvent, recurrentEvent);
            switch (recurrentEvent.frequency) {
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
                default:
                    logger.info("Something spooky's going on!"); //Should not happen
                    break;
            }
            ephemeralEvents.add(ee);
        }
        return ephemeralEvents;
    }

    public SortedSet<Event> getDayEvents(LocalDate localDate) {
        TreeSet<Event> dayEvents = new TreeSet<>(StartComparator.INSTANCE);

        dayEvents.addAll(getMonthEvents(YearMonth.from(localDate)).stream()
                .filter(e -> e.happensOnDate(localDate))
                .collect(Collectors.toSet()));

        return dayEvents;
    }

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


    public void addEvent(Event event){
        if (event instanceof RecurrentEvent) {
            //Todo RecurrentEvent
            recurringEndingSet.add((RecurrentEvent) event);
        } else {
            startingSet.add(event);
            endingSet.add(event);
        }
        updateCache(event);
    }

    public void removeEvent(Event event) {
        if (event instanceof RecurrentEvent) {
            //Todo Finalize RecurrentEvent
            recurringEndingSet.remove(event);
        } else {
            startingSet.remove(event);
            endingSet.remove(event);
        }
        updateCache(event);
    }

    public void updateEvent(SingularEvent oldEvent, SingularEvent newEvent){
        //Todo update this
//        startingSet.remove(oldEvent);
//        startingSet.add(newEvent);
//        endingSet.remove(oldEvent);
//        endingSet.add(newEvent);
        //Todo handle caching
    }

    private void updateCache(Event event) {
        List<YearMonth> list = getMonthSpan(YearMonth.from(event.getStart()), YearMonth.from(event.getEnd()));
        for (YearMonth ym : list) {
            monthCache.remove(ym);
        }
    }

    /**
     * Gets the YearMonth objects included in the given range
     * @param start The earlier of the two YearMonths
     * @param end The later of the two YearMonths
     * @return List of YearMonths in the range
     */
    public static List<YearMonth> getMonthSpan(YearMonth start, YearMonth end) {
        ArrayList<YearMonth> list = new ArrayList<>();
        YearMonth temp = YearMonth.from(start);
        while (temp.isBefore(end)) {
            list.add(temp);
            temp = temp.plusMonths(1);
        }
        list.add(end);
        return list;
    }

    //You can ignore these enums. They're a workaround to serialize lambdas.
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

    private enum RecurrenceEndComparator implements Comparator<RecurrentEvent> {
        INSTANCE;
        @Override
        public int compare(RecurrentEvent o1, RecurrentEvent o2) {
            int c = o1.getRecurrenceEnd().compareTo(o2.getRecurrenceEnd());
            if (c != 0) {
                return c;
            } else {
                return o1.getEventName().compareToIgnoreCase(o2.getEventName());
            }
        }
    }
}

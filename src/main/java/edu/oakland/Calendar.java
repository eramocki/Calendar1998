package edu.oakland;

import java.io.Serializable;
import java.time.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Calendar implements Serializable {

    private transient static final Logger logger = Logger.getLogger(Calendar.class.getName());

    public HashMap<YearMonth, Set<Event>> monthCache;

    public TreeSet<Event> startingSet;
    public TreeSet<Event> endingSet;
    private TreeSet<RecurrentEvent> recurringStartingSet;
    private TreeSet<RecurrentEvent> recurringEndingSet;

    public Calendar() {
        startingSet = new TreeSet<>(StartComparator.INSTANCE);
        endingSet = new TreeSet<>(EndComparator.INSTANCE);
        monthCache = new HashMap<>();
    }

    public Set<Event> getMonthEvents(YearMonth yearMonth) {
        return monthCache.computeIfAbsent(yearMonth, this::computeMonthEvents);
    }

    private Set<Event> computeMonthEvents(YearMonth yearMonth) {
        ZonedDateTime starting = ZonedDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1, 0, 0,
                0, 0, ZoneId.systemDefault());
        ZonedDateTime ending = ZonedDateTime.of(yearMonth.atEndOfMonth(), LocalTime.MAX, ZoneId.systemDefault());
        SingularEvent earliest = new SingularEvent(starting, starting, "");
        Set<Event> union = new HashSet<>();
        union.addAll(endingSet.tailSet(earliest).stream().filter(e -> e.getStart().compareTo(ending) < 0)
                .collect(Collectors.toSet()));
        return union;
    }

    public SortedSet<Event> getDayEvents(LocalDate localDate) {
        TreeSet<Event> dayEvents = new TreeSet<>(StartComparator.INSTANCE);

        // Do NOT change the formatting on this
        // :)
        Set<RecurrentEvent> recurringEvents = startingSet.stream()
                .filter(e -> e.getFrequency() != Frequency.NEVER)
                .map(obj -> (RecurrentEvent) obj) //Todo Update Once RecurrentEvents finalized
                .filter(e -> e.getRecurrenceBegin().isBefore(localDate.atStartOfDay(ZoneId.systemDefault())))
                .filter(e -> !e.getRecurrenceEnd().isBefore(localDate.atStartOfDay(ZoneId.systemDefault())))
                .collect(Collectors.toSet());

        TreeSet<Event> ephemeralEvents = new TreeSet<>(StartComparator.INSTANCE);

        for (RecurrentEvent recurringEvent : recurringEvents) {
            ZonedDateTime startOfEvent = recurringEvent.getStart();
            ZonedDateTime endOfEvent = recurringEvent.getEnd();

            while (endOfEvent.isBefore(recurringEvent.getRecurrenceEnd()) ) { //&& endOfEvent.isBefore(localDate.atStartOfDay(ZoneId.systemDefault()).plusHours(23))
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
                    default:
                        logger.info("Something spooky's going on!"); //Should not happen
                        break;
                }
                ephemeralEvents.add(ee);
            }
        }

        Set<Event> ephemeralEventsWithinDay = ephemeralEvents.stream()
                .filter(e -> e.happensOnDate(localDate))
                .collect(Collectors.toSet());

        dayEvents.addAll(ephemeralEventsWithinDay);

        dayEvents.addAll(startingSet.stream()
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
            startingSet.add(event);
            endingSet.add(event);
        } else {
            startingSet.add(event);
            endingSet.add(event);
        }
        updateCache(event);
    }

    public void removeEvent(Event event) {
        if (event instanceof RecurrentEvent) {
            //Todo Finalize RecurrentEvent
            startingSet.remove(event);
            endingSet.remove(event);
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
}

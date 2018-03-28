package edu.oakland;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CalendarTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getMonthEvents() {
        Calendar c = new Calendar();
        String[] arr = new String[]{"2018-02-03T10:15:30+01:00[Europe/Paris]","2018-03-17T10:15:30+01:00[Europe/Paris]",
                "2018-02-03T10:15:30+01:00[Europe/Paris]", "2018-02-27T10:15:30+01:00[Europe/Paris]",
                "2018-03-03T10:15:30+01:00[Europe/Paris]", "2018-03-16T10:15:30+01:00[Europe/Paris]",
                "2018-03-14T10:15:30+01:00[Europe/Paris]", "2018-04-18T10:15:30+01:00[Europe/Paris]",
                "2018-04-04T10:15:30+01:00[Europe/Paris]", "2018-04-27T10:15:30+01:00[Europe/Paris]",
                "2018-02-03T10:15:30+01:00[Europe/Paris]", "2018-04-27T10:15:30+01:00[Europe/Paris]"};
        List<String> successes = new ArrayList<>(Arrays.asList("1", "3", "4", "dup", "6"));
        for (int i = 0; i < arr.length; i += 2) {
            SingularEvent e = new SingularEvent(ZonedDateTime.parse(arr[i]), ZonedDateTime.parse(arr[i + 1]),
                    Integer.toString(i / 2 + 1));
            c.addEvent(e);
        }
        SingularEvent duplicateTime = new SingularEvent(ZonedDateTime.parse(arr[0]),  ZonedDateTime.parse(arr[1]), "dup");
        c.addEvent(duplicateTime);
        Set<Event> resultSet = c.getMonthEvents(YearMonth.of(2018, 3));
        assertTrue(resultSet.size() == successes.size());
        for (Event e : c.startingSet) {
            if (successes.contains(e.getEventName())) {
                assertTrue(resultSet.contains(e));
            } else {
                assertFalse(resultSet.contains(e));
            }
        }
    }

    @Test
    public void getDayEvents() {
        Calendar c = new Calendar();
        String[] arr = new String[]{"2018-02-03T10:15:30+01:00[Europe/Paris]", "2018-03-17T10:15:30+01:00[Europe/Paris]",
                "2018-02-03T10:15:30+01:00[Europe/Paris]", "2018-02-27T10:15:30+01:00[Europe/Paris]",
                "2018-03-03T10:15:30+01:00[Europe/Paris]", "2018-03-16T10:15:30+01:00[Europe/Paris]",
                "2018-03-14T10:15:30+01:00[Europe/Paris]", "2018-04-18T10:15:30+01:00[Europe/Paris]",
                "2018-04-04T10:15:30+01:00[Europe/Paris]", "2018-04-27T10:15:30+01:00[Europe/Paris]",
                "2017-02-01T10:18:30+01:00[Europe/Paris]", "2017-02-03T10:15:31+01:00[Europe/Paris]",
                "2017-02-01T10:15:30+01:00[Europe/Paris]", "2017-02-26T10:15:31+01:00[Europe/Paris]"};
        for (int i = 0; i < arr.length; i += 2) {
            SingularEvent e = new SingularEvent(ZonedDateTime.parse(arr[i]), ZonedDateTime.parse(arr[i + 1]),
                    Integer.toString(i / 2 + 1));
            c.addEvent(e);
        }
        SingularEvent duplicateTime = new SingularEvent(ZonedDateTime.parse(arr[0]),  ZonedDateTime.parse(arr[1]), "dup");
        c.addEvent(duplicateTime);

        Set<Event> resultSet1 = c.getDayEvents(LocalDate.of(2018, 2, 3));
        assertEquals(3, resultSet1.size());
        for (Event e : resultSet1) {
            if (e.getEventName().equals("1") || e.getEventName().equals("2") ||
                    e.getEventName().equals("dup")) {
                assertTrue(resultSet1.contains(e));
            } else {
                assertFalse(resultSet1.contains(e));
            }
        }

        Set<Event> resultSet2 = c.getDayEvents(LocalDate.of(2018, 4, 6));
        assertEquals(2, resultSet2.size());

        int iResSet2 = 1;
        for (Event e : resultSet2) {
            if (iResSet2 == 1) {
                assertEquals("First event should be '4'", "4", e.getEventName());
                iResSet2++;
            } else if (iResSet2 == 2) {
                assertEquals("Second event should be '5'", "5", e.getEventName());
                iResSet2++;
            }
            if (e.getEventName().equals("4") || e.getEventName().equals("5")) {
                assertTrue(resultSet2.contains(e));
            } else {
                assertFalse(resultSet2.contains(e));
            }
        }

        Set<Event> resultSet3 = c.getDayEvents(LocalDate.of(2018, 4, 4));
        assertEquals(2, resultSet3.size());
        for (Event e : resultSet3) {
            if (e.getEventName().equals("4")|| e.getEventName().equals("5")) {
                assertTrue(resultSet3.contains(e));
            } else {
                assertFalse(resultSet3.contains(e));
            }
        }

        Set<Event> resultSet4 = c.getDayEvents(LocalDate.of(2017, 2, 1));
        assertEquals(2, resultSet4.size());
        int iResSet4 = 1;
        for (Event e: resultSet4) {
            switch (iResSet4) {
                case 1:
                    assertEquals("First event should be '7'", "7", e.getEventName());
                    iResSet4++;
                    break;
                case 2:
                    assertEquals("Second event should be '6'", "6", e.getEventName());
                    iResSet4++;
                    break;
                default:
                    assertTrue("should not get here", false);
            }
        }

        // Recurrence
        Calendar c2 = new Calendar();

        Event recur1 = new RecurrentEvent(ZonedDateTime.parse("2017-02-01T10:15:30+01:00[Europe/Paris]"),
                ZonedDateTime.parse("2017-02-01T10:18:30+01:00[Europe/Paris]"),
                "recurEvent1", Frequency.DAILY,
                ZonedDateTime.parse("2017-02-01T10:15:30+01:00[Europe/Paris]"),
                ZonedDateTime.parse("2017-02-26T10:15:00+01:00[Europe/Paris]"));

        c2.addEvent(recur1);

        assertTrue("Recurrence ended on this day, but before the event started", c2.getDayEvents(LocalDate.of(2017, 2, 26)).isEmpty());

        for (int i = 1; i < 26; i++){
            Set<Event> rs = c2.getDayEvents(LocalDate.of(2017, 2, i));

            assertEquals(1, rs.size());
        }


        Calendar c3 = new Calendar();

        SingularEvent recur2 = new RecurrentEvent(ZonedDateTime.parse("2017-02-01T10:15:30+01:00[Europe/Paris]"),
                ZonedDateTime.parse("2017-02-01T10:18:30+01:00[Europe/Paris]"),
                "recurEvent1", Frequency.WEEKLY,
                ZonedDateTime.parse("2017-02-01T10:15:30+01:00[Europe/Paris]"),
                ZonedDateTime.parse("2017-02-26T10:15:00+01:00[Europe/Paris]"));

        c3.addEvent(recur2);

        for (int i = 1; i < 28; i++){
            Set<Event> rs = c3.getDayEvents(LocalDate.of(2017, 2, i));

            assertEquals((i - 1) % 7 == 0 ? 1 : 0, rs.size());
        }




        Calendar c4 = new Calendar();

        SingularEvent recur3 = new RecurrentEvent(ZonedDateTime.parse("2017-01-01T10:15:30+01:00[Europe/Paris]"),
                ZonedDateTime.parse("2017-01-08T10:18:30+01:00[Europe/Paris]"), "recurEvent1", Frequency.MONTHLY,
                ZonedDateTime.parse("2017-01-01T10:10:30+01:00[Europe/Paris]"),
                ZonedDateTime.parse("2018-01-02T10:15:00+01:00[Europe/Paris]"));

        c4.addEvent(recur3);

        for (int month = 1; month <= 12; month++){
            for (int day = 1; day <= YearMonth.of(2017, month).lengthOfMonth(); day++) {
                Set<Event> rs = c4.getDayEvents(LocalDate.of(2017, month, day));

                if (day <= 8) {
                    assertEquals(1, rs.size());
                } else {
                    assertEquals(0, rs.size());
                }
            }
        }
        assertEquals("recurrence ended midway through the event duration",0, c4.getDayEvents(LocalDate.of(2018, 1, 1)).size());
        assertEquals(0, c4.getDayEvents(LocalDate.of(2018, 2, 1)).size());

    }

    @Test
    public void getMonthSpan() {
        String[] ym1 = new String[] {"2017-01", "2017-06", "2017-12"};
        String[] ym2 = new String[] {"2017-05", "2017-12", "2018-01"};
        int[] goalLengths = new int[] {5, 7, 2};

        List<YearMonth> y1 = Arrays.stream(ym1).map(YearMonth::parse).collect(Collectors.toList());
        List<YearMonth> y2 = Arrays.stream(ym2).map(YearMonth::parse).collect(Collectors.toList());

        for (int i = 0; i < ym1.length; i++) {
            List<YearMonth> list = Calendar.getMonthSpan(y1.get(i), y2.get(i));
            assertEquals(list.size(), goalLengths[i]);
        }
    }

    @Test
    public void checkProperCaching() {
        Calendar c = new Calendar();
        YearMonth[] ym1 = new YearMonth[] {YearMonth.of(2017, 1), YearMonth.of(2017, 6),
        YearMonth.of(2017, 3), YearMonth.of(2016, 7)};
        for (YearMonth y : ym1) {
            c.getMonthEvents(y);
        }
        assertEquals(c.monthCache.size(), 4);
        Event e = new SingularEvent(ZonedDateTime.parse("2017-01-08T10:18:30+01:00[Europe/Paris]"),
                ZonedDateTime.parse("2017-03-12T10:18:30+01:00[Europe/Paris]"), "example");
        c.addEvent(e);
        assertEquals(c.monthCache.size(), 2);
    }
}
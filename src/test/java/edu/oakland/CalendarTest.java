package edu.oakland;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Set;

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
                "2018-04-04T10:15:30+01:00[Europe/Paris]", "2018-04-27T10:15:30+01:00[Europe/Paris]"};
        for (int i = 0; i < arr.length; i += 2) {
            Event e = new Event(ZonedDateTime.parse(arr[i]), ZonedDateTime.parse(arr[i + 1]),
                    Integer.toString(i / 2 + 1));
            c.addEvent(e);
        }
        Event duplicateTime = new Event(ZonedDateTime.parse(arr[0]),  ZonedDateTime.parse(arr[1]), "dup");
        c.addEvent(duplicateTime);
        Set<Event> resultSet = c.getMonthEvents(YearMonth.of(2018, 3));
        assertTrue(resultSet.size() == 4);
        for (Event e : c.startingSet) {
            if (e.getEventName().equals("1") || e.getEventName().equals("3") || e.getEventName().equals("4") ||
                    e.getEventName().equals("dup")) {
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
            Event e = new Event(ZonedDateTime.parse(arr[i]), ZonedDateTime.parse(arr[i + 1]),
                    Integer.toString(i / 2 + 1));
            c.addEvent(e);
        }
        Event duplicateTime = new Event(ZonedDateTime.parse(arr[0]),  ZonedDateTime.parse(arr[1]), "dup");
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
    }
}
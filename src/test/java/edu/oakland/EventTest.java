package edu.oakland;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class EventTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void happensSometimeOnDate() {
//        String[] arr = new String[]{"2018-02-03T10:15:30+01:00[Europe/Paris]","2018-03-17T10:15:30+01:00[Europe/Paris]",
//                "2018-02-03T10:15:30+01:00[Europe/Paris]", "2018-02-27T10:15:30+01:00[Europe/Paris]",
//                "2018-03-03T10:15:30+01:00[Europe/Paris]", "2018-03-16T10:15:30+01:00[Europe/Paris]",
//                "2018-03-14T10:15:30+01:00[Europe/Paris]", "2018-04-18T10:15:30+01:00[Europe/Paris]",
//                "2018-04-04T10:15:30+01:00[Europe/Paris]", "2018-04-27T10:15:30+01:00[Europe/Paris]"};
        SingularEvent e1 = new SingularEvent(ZonedDateTime.parse("2018-02-03T10:15:30+01:00[Europe/Paris]"), ZonedDateTime.parse("2018-03-17T10:15:30+01:00[Europe/Paris]"), "1");
        assertTrue(e1.happensOnDate(LocalDate.parse("2018-03-03")));
        assertTrue(e1.happensOnDate(LocalDate.parse("2018-02-03")));
        assertTrue(e1.happensOnDate(LocalDate.parse("2018-03-17")));
        assertFalse(e1.happensOnDate(LocalDate.parse("2018-03-18")));
        assertFalse(e1.happensOnDate(LocalDate.parse("2018-01-01")));

        SingularEvent e2 = new SingularEvent(ZonedDateTime.parse("2018-02-27T10:15:30+01:00[Europe/Paris]"), ZonedDateTime.parse("2018-02-27T11:15:30+01:00[Europe/Paris]"), "2");
        assertTrue(e2.happensOnDate(LocalDate.parse("2018-02-27")));
        assertFalse(e2.happensOnDate(LocalDate.parse("2018-02-26")));
        assertFalse(e2.happensOnDate(LocalDate.parse("2018-02-28")));

        SingularEvent e3 = new SingularEvent(
                ZonedDateTime.of(2018,2,27,0,0,0, 0, ZoneId.systemDefault()),
                ZonedDateTime.of(2018,2,27,11,15,30, 0, ZoneId.systemDefault()), "3");
        assertTrue(e3.happensOnDate(LocalDate.parse("2018-02-27")));
        assertFalse(e3.happensOnDate(LocalDate.parse("2018-02-28")));
        assertFalse(e3.happensOnDate(LocalDate.parse("2018-02-26")));

        SingularEvent e4 = new SingularEvent(
                ZonedDateTime.of(2018,2,26,18,0,0,0, ZoneId.systemDefault()),
                ZonedDateTime.of(2018,2,27,0,0,0,0, ZoneId.systemDefault()), "4");
        assertTrue(e4.happensOnDate(LocalDate.parse("2018-02-26")));
        assertFalse(e4.happensOnDate(LocalDate.parse("2018-02-27")));
        assertFalse(e4.happensOnDate(LocalDate.parse("2018-02-25")));

        SingularEvent e5 = new SingularEvent(
                ZonedDateTime.of(2018,2,26,0,0,0,0, ZoneId.systemDefault()),
                ZonedDateTime.of(2018,2,27,0,0,0,0, ZoneId.systemDefault()), "5");
        assertTrue(e5.happensOnDate(LocalDate.parse("2018-02-26")));
        assertFalse(e5.happensOnDate(LocalDate.parse("2018-02-27")));
        assertFalse(e5.happensOnDate(LocalDate.parse("2018-02-25")));
    }
}
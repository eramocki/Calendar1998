package edu.oakland;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
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
        Event e1 = new Event(ZonedDateTime.parse("2018-02-03T10:15:30+01:00[Europe/Paris]"), ZonedDateTime.parse("2018-03-17T10:15:30+01:00[Europe/Paris]"), "1");
        assertTrue(e1.happensSometimeOnDate(LocalDate.parse("2018-03-03")));
        assertTrue(e1.happensSometimeOnDate(LocalDate.parse("2018-02-03")));
        assertTrue(e1.happensSometimeOnDate(LocalDate.parse("2018-03-17")));
        assertFalse(e1.happensSometimeOnDate(LocalDate.parse("2018-03-18")));
        assertFalse(e1.happensSometimeOnDate(LocalDate.parse("2018-01-01")));

        Event e2 = new Event(ZonedDateTime.parse("2018-02-27T10:15:30+01:00[Europe/Paris]"), ZonedDateTime.parse("2018-02-27T11:15:30+01:00[Europe/Paris]"), "2");
        assertTrue(e2.happensSometimeOnDate(LocalDate.parse("2018-02-27")));
        assertFalse(e2.happensSometimeOnDate(LocalDate.parse("2018-02-26")));
        assertFalse(e2.happensSometimeOnDate(LocalDate.parse("2018-02-28")));
    }
}
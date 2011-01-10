/**
 * Copyright (C) 2009 - Present by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.timeseries;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import javax.time.calendar.LocalDate;
import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.util.time.DateUtil;

/**
 * 
 */
public class EndOfMonthAnnualScheduleCalculatorTest {
  private static final EndOfMonthAnnualScheduleCalculator CALCULATOR = new EndOfMonthAnnualScheduleCalculator();

  @Test(expected = IllegalArgumentException.class)
  public void testNullStart1() {
    CALCULATOR.getSchedule(null, LocalDate.of(2011, 1, 1), true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullStart2() {
    CALCULATOR.getSchedule(null, DateUtil.getUTCDate(2010, 1, 1), true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullEnd1() {
    CALCULATOR.getSchedule(LocalDate.of(2000, 1, 1), null, true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullEnd2() {
    CALCULATOR.getSchedule(DateUtil.getUTCDate(2000, 1, 1), null, true, true);
  }

  @Test
  public void testShortSeries() {
    final LocalDate start1 = LocalDate.of(2009, 3, 31);
    final LocalDate end1 = LocalDate.of(2010, 2, 28);
    LocalDate[] schedule1 = CALCULATOR.getSchedule(start1, end1, true);
    assertEquals(schedule1.length, 1);
    assertEquals(schedule1[0], end1);
    schedule1 = CALCULATOR.getSchedule(start1, end1, false);
    assertEquals(schedule1.length, 1);
    assertEquals(schedule1[0], start1);
    final ZonedDateTime start2 = DateUtil.getUTCDate(2009, 3, 1);
    final ZonedDateTime end2 = DateUtil.getUTCDate(2010, 2, 28);
    ZonedDateTime[] schedule2 = CALCULATOR.getSchedule(start2, end2, true);
    assertEquals(schedule2.length, 1);
    assertEquals(schedule2[0], end2);
    schedule2 = CALCULATOR.getSchedule(start2, end2, false);
    assertEquals(schedule2.length, 1);
    assertEquals(schedule2[0], DateUtil.getUTCDate(2009, 3, 31));
  }

  @Test
  public void test1() {
    LocalDate start1 = LocalDate.of(1994, 8, 31);
    LocalDate end1 = LocalDate.of(2004, 8, 31);
    final LocalDate[] expected = new LocalDate[] {LocalDate.of(1994, 8, 31), LocalDate.of(1995, 8, 31), LocalDate.of(1996, 8, 31), LocalDate.of(1997, 8, 31), LocalDate.of(1998, 8, 31),
        LocalDate.of(1999, 8, 31), LocalDate.of(2000, 8, 31), LocalDate.of(2001, 8, 31), LocalDate.of(2002, 8, 31), LocalDate.of(2003, 8, 31), LocalDate.of(2004, 8, 31)};
    LocalDate[] result = CALCULATOR.getSchedule(start1, end1, true);
    assertArrayEquals(expected, result);
    result = CALCULATOR.getSchedule(start1, end1, false);
    assertArrayEquals(expected, result);
    start1 = LocalDate.of(1994, 6, 30);
    end1 = LocalDate.of(2004, 8, 31);
    final LocalDate[] backward = new LocalDate[] {LocalDate.of(1994, 8, 31), LocalDate.of(1995, 8, 31), LocalDate.of(1996, 8, 31), LocalDate.of(1997, 8, 31), LocalDate.of(1998, 8, 31),
        LocalDate.of(1999, 8, 31), LocalDate.of(2000, 8, 31), LocalDate.of(2001, 8, 31), LocalDate.of(2002, 8, 31), LocalDate.of(2003, 8, 31), LocalDate.of(2004, 8, 31)};
    result = CALCULATOR.getSchedule(start1, end1, true);
    assertArrayEquals(backward, result);
    final LocalDate[] forward = new LocalDate[] {LocalDate.of(1994, 6, 30), LocalDate.of(1995, 6, 30), LocalDate.of(1996, 6, 30), LocalDate.of(1997, 6, 30), LocalDate.of(1998, 6, 30),
        LocalDate.of(1999, 6, 30), LocalDate.of(2000, 6, 30), LocalDate.of(2001, 6, 30), LocalDate.of(2002, 6, 30), LocalDate.of(2003, 6, 30), LocalDate.of(2004, 6, 30)};
    result = CALCULATOR.getSchedule(start1, end1, false);
    assertArrayEquals(forward, result);
  }

  @Test
  public void test2() {
    ZonedDateTime start1 = DateUtil.getUTCDate(1994, 8, 31);
    ZonedDateTime end1 = DateUtil.getUTCDate(2004, 8, 31);
    final ZonedDateTime[] expected = new ZonedDateTime[] {DateUtil.getUTCDate(1994, 8, 31), DateUtil.getUTCDate(1995, 8, 31), DateUtil.getUTCDate(1996, 8, 31), DateUtil.getUTCDate(1997, 8, 31),
        DateUtil.getUTCDate(1998, 8, 31), DateUtil.getUTCDate(1999, 8, 31), DateUtil.getUTCDate(2000, 8, 31), DateUtil.getUTCDate(2001, 8, 31), DateUtil.getUTCDate(2002, 8, 31),
        DateUtil.getUTCDate(2003, 8, 31), DateUtil.getUTCDate(2004, 8, 31)};
    ZonedDateTime[] result = CALCULATOR.getSchedule(start1, end1, true);
    assertArrayEquals(expected, result);
    result = CALCULATOR.getSchedule(start1, end1, false);
    assertArrayEquals(expected, result);
    start1 = DateUtil.getUTCDate(1994, 6, 30);
    end1 = DateUtil.getUTCDate(2004, 8, 31);
    final ZonedDateTime[] backward = new ZonedDateTime[] {DateUtil.getUTCDate(1994, 8, 31), DateUtil.getUTCDate(1995, 8, 31), DateUtil.getUTCDate(1996, 8, 31), DateUtil.getUTCDate(1997, 8, 31),
        DateUtil.getUTCDate(1998, 8, 31), DateUtil.getUTCDate(1999, 8, 31), DateUtil.getUTCDate(2000, 8, 31), DateUtil.getUTCDate(2001, 8, 31), DateUtil.getUTCDate(2002, 8, 31),
        DateUtil.getUTCDate(2003, 8, 31), DateUtil.getUTCDate(2004, 8, 31)};
    result = CALCULATOR.getSchedule(start1, end1, true);
    assertArrayEquals(backward, result);
    final ZonedDateTime[] forward = new ZonedDateTime[] {DateUtil.getUTCDate(1994, 6, 30), DateUtil.getUTCDate(1995, 6, 30), DateUtil.getUTCDate(1996, 6, 30), DateUtil.getUTCDate(1997, 6, 30),
        DateUtil.getUTCDate(1998, 6, 30), DateUtil.getUTCDate(1999, 6, 30), DateUtil.getUTCDate(2000, 6, 30), DateUtil.getUTCDate(2001, 6, 30), DateUtil.getUTCDate(2002, 6, 30),
        DateUtil.getUTCDate(2003, 6, 30), DateUtil.getUTCDate(2004, 6, 30)};
    result = CALCULATOR.getSchedule(start1, end1, false);
    assertArrayEquals(forward, result);
  }
}

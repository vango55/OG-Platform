/**
 * Copyright (C) 2009 - Present by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.timeseries;

import javax.time.calendar.LocalDate;
import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.util.time.DateUtil;

/**
 * 
 */
public abstract class ScheduleCalculatorTestCase {
  private static final LocalDate START1 = LocalDate.of(2000, 1, 1);
  private static final ZonedDateTime START2 = DateUtil.getUTCDate(2000, 1, 1);
  private static final LocalDate END1 = LocalDate.of(2000, 12, 1);
  private static final ZonedDateTime END2 = DateUtil.getUTCDate(2000, 12, 1);

  public abstract Schedule getScheduleCalculator();

  @Test(expected = IllegalArgumentException.class)
  public void testNullStartDate1() {
    getScheduleCalculator().getSchedule(null, END1, true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullStartDate2() {
    getScheduleCalculator().getSchedule(null, END2, true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullEndDate1() {
    getScheduleCalculator().getSchedule(START1, null, true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullEndDate2() {
    getScheduleCalculator().getSchedule(START2, null, true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartAfterEnd1() {
    getScheduleCalculator().getSchedule(END1, START1, true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartAfterEnd2() {
    getScheduleCalculator().getSchedule(END2, START2, true, true);
  }

}

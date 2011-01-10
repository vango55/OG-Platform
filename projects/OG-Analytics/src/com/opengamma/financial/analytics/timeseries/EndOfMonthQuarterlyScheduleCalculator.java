/**
 * Copyright (C) 2009 - Present by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.timeseries;

import java.util.ArrayList;
import java.util.List;

import javax.time.calendar.LocalDate;
import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.Validate;

/**
 * 
 */
public class EndOfMonthQuarterlyScheduleCalculator extends Schedule {
  private static final EndOfMonthScheduleCalculator EOM_CALCULATOR = ScheduleCalculatorFactory.END_OF_MONTH_CALCULATOR;

  @Override
  public LocalDate[] getSchedule(final LocalDate startDate, final LocalDate endDate, final boolean fromEnd, final boolean generateRecursive) {
    return getSchedule(startDate, endDate, fromEnd);
  }

  public LocalDate[] getSchedule(final LocalDate startDate, final LocalDate endDate, final boolean fromEnd) {
    Validate.notNull(startDate, "start date");
    Validate.notNull(endDate, "end date");
    final LocalDate[] monthly = EOM_CALCULATOR.getSchedule(startDate, endDate);
    final List<LocalDate> result = new ArrayList<LocalDate>();
    if (fromEnd) {
      for (int i = monthly.length - 1; i >= 0; i -= 3) {
        result.add(monthly[i]);
      }
      return getReversedDates(result);
    }
    for (int i = 0; i < monthly.length; i += 3) {
      result.add(monthly[i]);
    }
    return result.toArray(EMPTY_LOCAL_DATE_ARRAY);
  }

  @Override
  public ZonedDateTime[] getSchedule(final ZonedDateTime startDate, final ZonedDateTime endDate, final boolean fromEnd, final boolean generateRecursive) {
    return getSchedule(startDate, endDate, fromEnd);
  }

  public ZonedDateTime[] getSchedule(final ZonedDateTime startDate, final ZonedDateTime endDate, final boolean fromEnd) {
    Validate.notNull(startDate, "start date");
    Validate.notNull(endDate, "end date");
    final ZonedDateTime[] monthly = EOM_CALCULATOR.getSchedule(startDate, endDate);
    final List<ZonedDateTime> result = new ArrayList<ZonedDateTime>();
    if (fromEnd) {
      for (int i = monthly.length - 1; i >= 0; i -= 3) {
        result.add(monthly[i]);
      }
      return getReversedDates(result);
    }
    for (int i = 0; i < monthly.length; i += 3) {
      result.add(monthly[i]);
    }
    return result.toArray(EMPTY_ZONED_DATE_TIME_ARRAY);
  }
}

/**
 * Copyright (C) 2009 - Present by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.covariance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.opengamma.util.CalculationMode;

/**
 * 
 */
public class ExponentialWeightedMovingAverageHistoricalVolatilityCalculatorTest extends HistoricalVolatilityCalculatorTestCase {
  private static final HistoricalVolatilityCalculator CALCULATOR = new ExponentialWeightedMovingAverageHistoricalVolatilityCalculator(0.94, RETURN_CALCULATOR);

  @Test(expected = IllegalArgumentException.class)
  public void testNullCalculator() {
    new ExponentialWeightedMovingAverageHistoricalVolatilityCalculator(0.3, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCalculationMode() {
    new ExponentialWeightedMovingAverageHistoricalVolatilityCalculator(0.4, RETURN_CALCULATOR, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeLambda() {
    new ExponentialWeightedMovingAverageHistoricalVolatilityCalculator(-0.95, RETURN_CALCULATOR);
  }

  @Test
  public void test() {
    assertEquals(Math.sqrt(252) * CALCULATOR.evaluate(CLOSE_TS), 0.2455, EPS);
  }

  @Override
  protected HistoricalVolatilityCalculator getCalculator() {
    return CALCULATOR;
  }

  @Test
  public void testObject() {
    HistoricalVolatilityCalculator other = new ExponentialWeightedMovingAverageHistoricalVolatilityCalculator(0.94, RETURN_CALCULATOR);
    assertEquals(CALCULATOR, other);
    assertEquals(CALCULATOR.hashCode(), other.hashCode());
    other = new ExponentialWeightedMovingAverageHistoricalVolatilityCalculator(0.94, RETURN_CALCULATOR, CalculationMode.STRICT);
    assertEquals(CALCULATOR, other);
    assertEquals(CALCULATOR.hashCode(), other.hashCode());
    assertEquals(CALCULATOR.hashCode(), other.hashCode());
    other = new ExponentialWeightedMovingAverageHistoricalVolatilityCalculator(0.95, RETURN_CALCULATOR, CalculationMode.STRICT);
    assertFalse(CALCULATOR.equals(other));
    other = new ExponentialWeightedMovingAverageHistoricalVolatilityCalculator(0.94, RELATIVE_RETURN_CALCULATOR, CalculationMode.STRICT);
    assertFalse(CALCULATOR.equals(other));
    other = new ExponentialWeightedMovingAverageHistoricalVolatilityCalculator(0.94, RETURN_CALCULATOR, CalculationMode.LENIENT);
    assertFalse(CALCULATOR.equals(other));
  }
}

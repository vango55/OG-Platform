/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.curve.exposure;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.financial.security.bond.CorporateBondSecurity;
import com.opengamma.financial.security.bond.GovernmentBondSecurity;
import com.opengamma.financial.security.bond.MunicipalBondSecurity;
import com.opengamma.financial.security.capfloor.CapFloorCMSSpreadSecurity;
import com.opengamma.financial.security.capfloor.CapFloorSecurity;
import com.opengamma.financial.security.cash.CashSecurity;
import com.opengamma.financial.security.cashflow.CashFlowSecurity;
import com.opengamma.financial.security.cds.CreditDefaultSwapIndexSecurity;
import com.opengamma.financial.security.cds.LegacyFixedRecoveryCDSSecurity;
import com.opengamma.financial.security.cds.LegacyRecoveryLockCDSSecurity;
import com.opengamma.financial.security.cds.LegacyVanillaCDSSecurity;
import com.opengamma.financial.security.cds.StandardFixedRecoveryCDSSecurity;
import com.opengamma.financial.security.cds.StandardRecoveryLockCDSSecurity;
import com.opengamma.financial.security.cds.StandardVanillaCDSSecurity;
import com.opengamma.financial.security.deposit.ContinuousZeroDepositSecurity;
import com.opengamma.financial.security.deposit.PeriodicZeroDepositSecurity;
import com.opengamma.financial.security.deposit.SimpleZeroDepositSecurity;
import com.opengamma.financial.security.equity.EquitySecurity;
import com.opengamma.financial.security.equity.EquityVarianceSwapSecurity;
import com.opengamma.financial.security.forward.AgricultureForwardSecurity;
import com.opengamma.financial.security.forward.EnergyForwardSecurity;
import com.opengamma.financial.security.forward.MetalForwardSecurity;
import com.opengamma.financial.security.fra.FRASecurity;
import com.opengamma.financial.security.future.AgricultureFutureSecurity;
import com.opengamma.financial.security.future.BondFutureSecurity;
import com.opengamma.financial.security.future.DeliverableSwapFutureSecurity;
import com.opengamma.financial.security.future.EnergyFutureSecurity;
import com.opengamma.financial.security.future.EquityFutureSecurity;
import com.opengamma.financial.security.future.EquityIndexDividendFutureSecurity;
import com.opengamma.financial.security.future.FXFutureSecurity;
import com.opengamma.financial.security.future.IndexFutureSecurity;
import com.opengamma.financial.security.future.InterestRateFutureSecurity;
import com.opengamma.financial.security.future.MetalFutureSecurity;
import com.opengamma.financial.security.future.StockFutureSecurity;
import com.opengamma.financial.security.fx.FXForwardSecurity;
import com.opengamma.financial.security.fx.NonDeliverableFXForwardSecurity;
import com.opengamma.financial.security.option.BondFutureOptionSecurity;
import com.opengamma.financial.security.option.CommodityFutureOptionSecurity;
import com.opengamma.financial.security.option.CreditDefaultSwapOptionSecurity;
import com.opengamma.financial.security.option.EquityBarrierOptionSecurity;
import com.opengamma.financial.security.option.EquityIndexDividendFutureOptionSecurity;
import com.opengamma.financial.security.option.EquityIndexFutureOptionSecurity;
import com.opengamma.financial.security.option.EquityIndexOptionSecurity;
import com.opengamma.financial.security.option.EquityOptionSecurity;
import com.opengamma.financial.security.option.FXBarrierOptionSecurity;
import com.opengamma.financial.security.option.FXDigitalOptionSecurity;
import com.opengamma.financial.security.option.FXOptionSecurity;
import com.opengamma.financial.security.option.FxFutureOptionSecurity;
import com.opengamma.financial.security.option.IRFutureOptionSecurity;
import com.opengamma.financial.security.option.NonDeliverableFXDigitalOptionSecurity;
import com.opengamma.financial.security.option.NonDeliverableFXOptionSecurity;
import com.opengamma.financial.security.option.SwaptionSecurity;
import com.opengamma.financial.security.swap.ForwardSwapSecurity;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.financial.security.swap.YearOnYearInflationSwapSecurity;
import com.opengamma.financial.security.swap.ZeroCouponInflationSwapSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.util.test.TestGroup;

/**
 * 
 */
@Test(groups = TestGroup.UNIT)
public class UnderlyingExposureFunctionTest {
  private static final ExposureFunction EXPOSURE_FUNCTION = new UnderlyingExposureFunction(ExposureFunctionTestHelper.getSecuritySource(null));

  @Test
  public void testAgriculturalFutureSecurity() {
    final AgricultureFutureSecurity future = ExposureFunctionTestHelper.getAgricultureFutureSecurity();
    final List<ExternalId> ids = future.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testBondFutureOptionSecurity() {
    final BondFutureSecurity future = ExposureFunctionTestHelper.getBondFutureSecurity();
    final SecuritySource securitySource = ExposureFunctionTestHelper.getSecuritySource(future);
    final ExposureFunction exposureFunction = new UnderlyingExposureFunction(securitySource);
    final BondFutureOptionSecurity security = ExposureFunctionTestHelper.getBondFutureOptionSecurity();
    final List<ExternalId> ids = security.accept(exposureFunction);
    assertNull(ids);
  }

  @Test
  public void testBondFutureSecurity() {
    final BondFutureSecurity future = ExposureFunctionTestHelper.getBondFutureSecurity();
    final List<ExternalId> ids = future.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testCapFloorCMSSpreadSecurity() {
    final CapFloorCMSSpreadSecurity security = ExposureFunctionTestHelper.getCapFloorCMSSpreadSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(2, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("USD 10y Swap"), ids.get(0));
    assertEquals(ExternalSchemes.syntheticSecurityId("USD 15y Swap"), ids.get(1));
  }

  @Test
  public void testCapFloorSecurity() {
    final CapFloorSecurity security = ExposureFunctionTestHelper.getCapFloorSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("USD 6m Libor"), ids.get(0));
  }

  @Test
  public void testCashFlowSecurity() {
    final CashFlowSecurity security = ExposureFunctionTestHelper.getCashFlowSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testCashSecurity() {
    final CashSecurity cash = ExposureFunctionTestHelper.getCashSecurity();
    final List<ExternalId> ids = cash.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testContinuousZeroDepositSecurity() {
    final ContinuousZeroDepositSecurity security = ExposureFunctionTestHelper.getContinuousZeroDepositSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testCorporateBondSecurity() {
    final CorporateBondSecurity security = ExposureFunctionTestHelper.getCorporateBondSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEnergyFutureOptionSecurity() {
    final EnergyFutureSecurity future = ExposureFunctionTestHelper.getEnergyFutureSecurity();
    final SecuritySource securitySource = ExposureFunctionTestHelper.getSecuritySource(future);
    final ExposureFunction exposureFunction = new UnderlyingExposureFunction(securitySource);
    final CommodityFutureOptionSecurity security = ExposureFunctionTestHelper.getEnergyFutureOptionSecurity();
    final List<ExternalId> ids = security.accept(exposureFunction);
    assertNull(ids);
  }

  @Test
  public void testEnergyFutureSecurity() {
    final EnergyFutureSecurity future = ExposureFunctionTestHelper.getEnergyFutureSecurity();
    final List<ExternalId> ids = future.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEquityBarrierOptionSecurity() {
    final EquityBarrierOptionSecurity security = ExposureFunctionTestHelper.getEquityBarrierOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEquityFutureSecurity() {
    final EquityFutureSecurity future = ExposureFunctionTestHelper.getEquityFutureSecurity();
    final List<ExternalId> ids = future.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEquityIndexDividendFutureSecurity() {
    final SecuritySource securitySource = ExposureFunctionTestHelper.getSecuritySource(null);
    final EquityIndexDividendFutureSecurity future = ExposureFunctionTestHelper.getEquityIndexDividendFutureSecurity();
    final ExposureFunction exposureFunction = new UnderlyingExposureFunction(securitySource);
    final List<ExternalId> ids = future.accept(exposureFunction);
    assertNull(ids);
  }

  @Test
  public void testEquitySecurity() {
    final EquitySecurity security = ExposureFunctionTestHelper.getEquitySecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testFRASecurity() {
    final FRASecurity fra = ExposureFunctionTestHelper.getFRASecurity();
    final List<ExternalId> ids = fra.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(fra.getUnderlyingId(), ids.get(0));
  }

  @Test
  public void testFXFutureOptionSecurity() {
    final FXFutureSecurity future = ExposureFunctionTestHelper.getFXFutureSecurity();
    final SecuritySource securitySource = ExposureFunctionTestHelper.getSecuritySource(future);
    final ExposureFunction exposureFunction = new UnderlyingExposureFunction(securitySource);
    final FxFutureOptionSecurity security = ExposureFunctionTestHelper.getFXFutureOptionSecurity();
    final List<ExternalId> ids = security.accept(exposureFunction);
    assertNull(ids);
  }

  @Test
  public void testFXFutureSecurity() {
    final FXFutureSecurity future = ExposureFunctionTestHelper.getFXFutureSecurity();
    final List<ExternalId> ids = future.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testIndexFutureSecurity() {
    final IndexFutureSecurity future = ExposureFunctionTestHelper.getIndexFutureSecurity();
    final List<ExternalId> ids = future.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testInterestRateFutureSecurity() {
    final InterestRateFutureSecurity future = ExposureFunctionTestHelper.getInterestRateFutureSecurity();
    final List<ExternalId> ids = future.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("USD 3m Libor"), ids.get(0));
  }

  @Test
  public void testMetalFutureSecurity() {
    final MetalFutureSecurity future = ExposureFunctionTestHelper.getMetalFutureSecurity();
    final List<ExternalId> ids = future.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testStockFutureSecurity() {
    final StockFutureSecurity future = ExposureFunctionTestHelper.getStockFutureSecurity();
    final List<ExternalId> ids = future.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testAgricultureForwardSecurity() {
    final AgricultureForwardSecurity security = ExposureFunctionTestHelper.getAgricultureForwardSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testCreditDefaultSwapIndexSecurity() {
    final CreditDefaultSwapIndexSecurity security = ExposureFunctionTestHelper.getCreditDefaultSwapIndexSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testCreditDefaultSwapOptionSecurity() {
    final CreditDefaultSwapOptionSecurity security = ExposureFunctionTestHelper.getCreditDefaultSwapOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testDeliverableSwapSecurity() {
    final DeliverableSwapFutureSecurity security = ExposureFunctionTestHelper.getDeliverableSwapFutureSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEnergyForwardSecurity() {
    final EnergyForwardSecurity security = ExposureFunctionTestHelper.getEnergyForwardSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEquityIndexDividendFutureOptionSecurity() {
    final EquityIndexDividendFutureOptionSecurity security = ExposureFunctionTestHelper.getEquityIndexDividendFutureOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEquityIndexFutureOptionSecurity() {
    final EquityIndexFutureOptionSecurity security = ExposureFunctionTestHelper.getEquityIndexFutureOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEquityIndexOptionSecurity() {
    final EquityIndexOptionSecurity security = ExposureFunctionTestHelper.getEquityIndexOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEquityOptionSecurity() {
    final EquityOptionSecurity security = ExposureFunctionTestHelper.getEquityOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testEquityVarianceSwapSecurity() {
    final EquityVarianceSwapSecurity security = ExposureFunctionTestHelper.getEquityVarianceSwapSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testFixedFloatSwapSecurity() {
    SwapSecurity security = ExposureFunctionTestHelper.getPayFixedFloatSwapSecurity();
    List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("3m Euribor"), ids.get(0));
    security = ExposureFunctionTestHelper.getReceiveFixedFloatSwapSecurity();
    ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("3m Euribor"), ids.get(0));
  }

  @Test
  public void testFloatFloatSwapSecurity() {
    final SwapSecurity security = ExposureFunctionTestHelper.getFloatFloatSwapSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(2, ids.size());
    assertTrue(ids.containsAll(Arrays.asList(ExternalSchemes.syntheticSecurityId("3m Euribor"), ExternalSchemes.syntheticSecurityId("6m Euribor"))));
  }

  @Test
  public void testForwardFixedFloatSwapSecurity() {
    ForwardSwapSecurity security = ExposureFunctionTestHelper.getPayForwardFixedFloatSwapSecurity();
    List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("3m Euribor"), ids.get(0));
    security = ExposureFunctionTestHelper.getReceiveForwardFixedFloatSwapSecurity();
    ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("3m Euribor"), ids.get(0));
  }

  @Test
  public void testForwardFloatFloatSwapSecurity() {
    final ForwardSwapSecurity security = ExposureFunctionTestHelper.getForwardFloatFloatSwapSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(2, ids.size());
    assertTrue(ids.containsAll(Arrays.asList(ExternalSchemes.syntheticSecurityId("3m Euribor"), ExternalSchemes.syntheticSecurityId("6m Euribor"))));
  }

  @Test
  public void testForwardXCcySwapSecurity() {
    final ForwardSwapSecurity security = ExposureFunctionTestHelper.getForwardXCcySwapSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(2, ids.size());
    assertTrue(ids.containsAll(Arrays.asList(ExternalSchemes.syntheticSecurityId("6m Euribor"), ExternalSchemes.syntheticSecurityId("3m USD Libor"))));
  }

  @Test
  public void testFXBarrierOptionSecurity() {
    final FXBarrierOptionSecurity security = ExposureFunctionTestHelper.getFXBarrierOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testFXDigitalOptionSecurity() {
    final FXDigitalOptionSecurity security = ExposureFunctionTestHelper.getFXDigitalOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testFXForwardSecurity() {
    final FXForwardSecurity security = ExposureFunctionTestHelper.getFXForwardSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testFXOptionSecurity() {
    final FXOptionSecurity security = ExposureFunctionTestHelper.getFXOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testGovernmentBondSecurity() {
    final GovernmentBondSecurity security = ExposureFunctionTestHelper.getGovernmentBondSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testInterestRateFutureOptionSecurity() {
    final IRFutureOptionSecurity security = ExposureFunctionTestHelper.getInterestRateFutureOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testLegacyFixedRecoveryCDSSecurity() {
    final LegacyFixedRecoveryCDSSecurity security = ExposureFunctionTestHelper.getLegacyFixedRecoveryCDSSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testLegacyRecoveryLockCDSSecurity() {
    final LegacyRecoveryLockCDSSecurity security = ExposureFunctionTestHelper.getLegacyRecoveryLockCDSSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testLegacyVanillaCDSSecurity() {
    final LegacyVanillaCDSSecurity security = ExposureFunctionTestHelper.getLegacyVanillaCDSSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testMetalForwardSecurity() {
    final MetalForwardSecurity security = ExposureFunctionTestHelper.getMetalForwardSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testMunicipalBondSecurity() {
    final MunicipalBondSecurity security = ExposureFunctionTestHelper.getMunicipalBondSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testNonDeliverableFXDigitalOptionSecurity() {
    final NonDeliverableFXDigitalOptionSecurity security = ExposureFunctionTestHelper.getNonDeliverableFXDigitalOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testNonDeliverableFXForwardSecurity() {
    final NonDeliverableFXForwardSecurity security = ExposureFunctionTestHelper.getNonDeliverableFXForwardSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testNonDeliverableFXOptionSecurity() {
    final NonDeliverableFXOptionSecurity security = ExposureFunctionTestHelper.getNonDeliverableFXOptionSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testPeriodicZeroDepositSecurity() {
    final PeriodicZeroDepositSecurity security = ExposureFunctionTestHelper.getPeriodicZeroDepositSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testSimpleZeroDepositSecurity() {
    final SimpleZeroDepositSecurity security = ExposureFunctionTestHelper.getSimpleZeroDepositSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testStandardFixedRecoveryCDSSecurity() {
    final StandardFixedRecoveryCDSSecurity security = ExposureFunctionTestHelper.getStandardFixedRecoveryCDSSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testStandardRecoveryLockCDSSecurity() {
    final StandardRecoveryLockCDSSecurity security = ExposureFunctionTestHelper.getStandardRecoveryLockCDSSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testStandardVanillaCDSSecurity() {
    final StandardVanillaCDSSecurity security = ExposureFunctionTestHelper.getStandardVanillaCDSSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertNull(ids);
  }

  @Test
  public void testSwaptionSecurity() {
    SwapSecurity underlying = ExposureFunctionTestHelper.getPayFixedFloatSwapSecurity();
    SwaptionSecurity security = ExposureFunctionTestHelper.getPaySwaptionSecurity();
    List<ExternalId> ids = security.accept(new UnderlyingExposureFunction(ExposureFunctionTestHelper.getSecuritySource(underlying)));
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("3m Euribor"), ids.get(0));
    underlying = ExposureFunctionTestHelper.getReceiveFixedFloatSwapSecurity();
    security = ExposureFunctionTestHelper.getPaySwaptionSecurity();
    ids = security.accept(new UnderlyingExposureFunction(ExposureFunctionTestHelper.getSecuritySource(underlying)));
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("3m Euribor"), ids.get(0));
  }

  @Test
  public void testXCcySwapSecurity() {
    final SwapSecurity security = ExposureFunctionTestHelper.getXCcySwapSecurity();
    final List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(2, ids.size());
    assertTrue(ids.containsAll(Arrays.asList(ExternalSchemes.syntheticSecurityId("3m Euribor"), ExternalSchemes.syntheticSecurityId("3m USD Libor"))));
  }
  
  @Test
  public void testPayYoYInflationSwapSecurity() {
    YearOnYearInflationSwapSecurity security = ExposureFunctionTestHelper.getPayYoYInflationSwapSecurity();
    List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("CPI"), ids.get(0));
  }
  
  @Test
  public void testReceiveYoYInflationSwapSecurity() {
    YearOnYearInflationSwapSecurity security = ExposureFunctionTestHelper.getPayYoYInflationSwapSecurity();
    List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("CPI"), ids.get(0));
  }
  
  @Test
  public void testPayZeroCouponInflationSwapSecurity() {
    ZeroCouponInflationSwapSecurity security = ExposureFunctionTestHelper.getPayZeroCouponInflationSwapSecurity();
    List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("CPI"), ids.get(0));
  }
  
  @Test
  public void testReceiveZeroCouponInflationSwapSecurity() {
    ZeroCouponInflationSwapSecurity security = ExposureFunctionTestHelper.getReceiveZeroCouponInflationSwapSecurity();
    List<ExternalId> ids = security.accept(EXPOSURE_FUNCTION);
    assertEquals(1, ids.size());
    assertEquals(ExternalSchemes.syntheticSecurityId("CPI"), ids.get(0));
  }
}
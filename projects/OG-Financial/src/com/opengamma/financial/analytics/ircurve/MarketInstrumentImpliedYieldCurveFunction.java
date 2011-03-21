/*
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.ircurve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.calendar.Clock;
import javax.time.calendar.LocalDate;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.opengamma.OpenGammaRuntimeException;
<<<<<<< HEAD
import com.opengamma.core.common.CurrencyUnit;
import com.opengamma.core.exchange.ExchangeSource;
=======
>>>>>>> b70bb72f07503ccaa315cb05649b86ea88d2b720
import com.opengamma.core.holiday.HolidaySource;
import com.opengamma.core.region.RegionSource;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.core.security.SecurityUtils;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.OpenGammaCompilationContext;
import com.opengamma.financial.OpenGammaExecutionContext;
import com.opengamma.financial.analytics.fixedincome.CashSecurityConverter;
import com.opengamma.financial.analytics.fixedincome.FRASecurityConverter;
import com.opengamma.financial.analytics.fixedincome.FutureSecurityConverter;
import com.opengamma.financial.analytics.fixedincome.SwapSecurityConverter;
import com.opengamma.financial.analytics.swap.TenorSwapSecurityToTenorSwapConverter;
import com.opengamma.financial.convention.ConventionBundle;
import com.opengamma.financial.convention.ConventionBundleSource;
import com.opengamma.financial.convention.InMemoryConventionBundleMaster;
import com.opengamma.financial.instrument.FixedIncomeFutureInstrumentDefinition;
import com.opengamma.financial.instrument.FixedIncomeInstrumentDefinition;
import com.opengamma.financial.interestrate.InterestRateDerivative;
import com.opengamma.financial.interestrate.LastDateCalculator;
import com.opengamma.financial.interestrate.MultipleYieldCurveFinderDataBundle;
import com.opengamma.financial.interestrate.MultipleYieldCurveFinderFunction;
import com.opengamma.financial.interestrate.MultipleYieldCurveFinderJacobian;
import com.opengamma.financial.interestrate.ParRateCalculator;
import com.opengamma.financial.interestrate.ParRateCurveSensitivityCalculator;
import com.opengamma.financial.interestrate.PresentValueCalculator;
import com.opengamma.financial.interestrate.PresentValueSensitivityCalculator;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.FinancialSecurityVisitorAdapter;
import com.opengamma.financial.security.future.FutureSecurity;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.livedata.normalization.MarketDataRequirementNames;
import com.opengamma.math.ParallelArrayBinarySort;
import com.opengamma.math.curve.InterpolatedDoublesCurve;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.interpolation.CombinedInterpolatorExtrapolatorFactory;
import com.opengamma.math.interpolation.Interpolator1D;
import com.opengamma.math.interpolation.Interpolator1DFactory;
import com.opengamma.math.interpolation.data.Interpolator1DDataBundle;
import com.opengamma.math.interpolation.sensitivity.CombinedInterpolatorExtrapolatorNodeSensitivityCalculatorFactory;
import com.opengamma.math.interpolation.sensitivity.Interpolator1DNodeSensitivityCalculator;
import com.opengamma.math.linearalgebra.DecompositionFactory;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.matrix.DoubleMatrix2D;
import com.opengamma.math.rootfinding.newton.BroydenVectorRootFinder;
import com.opengamma.math.rootfinding.newton.NewtonVectorRootFinder;
import com.opengamma.util.money.Currency;

/**
 * 
 */
public class MarketInstrumentImpliedYieldCurveFunction extends AbstractFunction {

  private static final Logger s_logger = LoggerFactory.getLogger(MarketInstrumentImpliedYieldCurveFunction.class);

  private final Currency _currency;
  private final String _fundingCurveDefinitionName;
  private final String _forwardCurveDefinitionName;
  private YieldCurveDefinition _fundingCurveDefinition;
  private YieldCurveDefinition _forwardCurveDefinition;
  private InterpolatedYieldCurveSpecificationBuilder _curveSpecificationBuilder;
  private ValueSpecification _fundingCurveResult;
  private ValueSpecification _forwardCurveResult;
  private ValueSpecification _jacobianResult;
  private ValueSpecification _fundingCurveSpecResult;
  private ValueSpecification _forwardCurveSpecResult;
  private Set<ValueSpecification> _results;
  private static final LastDateCalculator LAST_DATE_CALCULATOR = LastDateCalculator.getInstance();

  public MarketInstrumentImpliedYieldCurveFunction(final String currency, final String curveDefinitionName) {
    this(currency, curveDefinitionName, curveDefinitionName);
  }

  public MarketInstrumentImpliedYieldCurveFunction(final String currency, final String fundingCurveDefinitionName, final String forwardCurveDefinitionName) {
    this(Currency.of(currency), fundingCurveDefinitionName, forwardCurveDefinitionName);
  }

  public MarketInstrumentImpliedYieldCurveFunction(final Currency currency, final String curveDefinitionName) {
    this(currency, curveDefinitionName, curveDefinitionName);
  }

  public MarketInstrumentImpliedYieldCurveFunction(final Currency currency, final String fundingCurveDefinitionName, final String forwardCurveDefinitionName) {
    Validate.notNull(currency, "curve currency");
    Validate.notNull(fundingCurveDefinitionName, "funding curve name");
    Validate.notNull(forwardCurveDefinitionName, "forward curve name");
    _currency = currency;
    _fundingCurveDefinitionName = fundingCurveDefinitionName;
    _forwardCurveDefinitionName = forwardCurveDefinitionName;
  }

  private YieldCurveDefinition getDefinition(final FunctionCompilationContext context, final String name) {
    final InterpolatedYieldCurveDefinitionSource curveDefinitionSource = OpenGammaCompilationContext
        .getInterpolatedYieldCurveDefinitionSource(context);
    final YieldCurveDefinition definition = curveDefinitionSource.getDefinition(_currency, name);
    if (definition == null) {
      s_logger.warn("No curve definition for {} on {}", name, _currency);
    } else {
      if (definition.getUniqueId() != null) {
        context.getFunctionReinitializer().reinitializeFunction(this, definition.getUniqueId());
      } else {
        s_logger.warn("Curve {} on {} has no identifier - cannot subscribe to updates", name, _currency);
      }
    }
    return definition;
  }

  @Override
  public void init(final FunctionCompilationContext context) {
    _fundingCurveDefinition = getDefinition(context, _fundingCurveDefinitionName);
    _forwardCurveDefinition = getDefinition(context, _forwardCurveDefinitionName);
    _curveSpecificationBuilder = OpenGammaCompilationContext.getInterpolatedYieldCurveSpecificationBuilder(context);
    final ComputationTargetSpecification currencySpec = new ComputationTargetSpecification(_currency);
    _fundingCurveResult = new ValueSpecification(ValueRequirementNames.YIELD_CURVE, currencySpec,
        createValueProperties().with(ValuePropertyNames.CURVE, _fundingCurveDefinitionName).get());
    _forwardCurveResult = new ValueSpecification(ValueRequirementNames.YIELD_CURVE, currencySpec,
        createValueProperties().with(ValuePropertyNames.CURVE, _forwardCurveDefinitionName).get());
    _jacobianResult = new ValueSpecification(ValueRequirementNames.YIELD_CURVE_JACOBIAN, currencySpec,
        createValueProperties().with(YieldCurveFunction.PROPERTY_FORWARD_CURVE, _forwardCurveDefinitionName)
            .with(YieldCurveFunction.PROPERTY_FUNDING_CURVE, _fundingCurveDefinitionName).get());
    _fundingCurveSpecResult = new ValueSpecification(ValueRequirementNames.YIELD_CURVE_SPEC, currencySpec,
        createValueProperties().with(ValuePropertyNames.CURVE, _fundingCurveDefinitionName).get());
    _forwardCurveSpecResult = new ValueSpecification(ValueRequirementNames.YIELD_CURVE_SPEC, currencySpec,
        createValueProperties().with(ValuePropertyNames.CURVE, _forwardCurveDefinitionName).get());
    _results = Sets.newHashSet(_fundingCurveResult, _forwardCurveResult, _jacobianResult, _fundingCurveSpecResult,
        _forwardCurveSpecResult);
  }

  public static Set<ValueRequirement> buildRequirements(final InterpolatedYieldCurveSpecification specification,
      final FunctionCompilationContext context) {
    final Set<ValueRequirement> result = new HashSet<ValueRequirement>();
    for (final FixedIncomeStripWithIdentifier strip : specification.getStrips()) {
      result.add(new ValueRequirement(MarketDataRequirementNames.MARKET_VALUE, strip.getSecurity()));
    }
    final ConventionBundleSource conventionBundleSource = OpenGammaCompilationContext
        .getConventionBundleSource(context);
    final ConventionBundle conventionBundle = conventionBundleSource.getConventionBundle(Identifier.of(
        InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, specification.getCurrency().getCode() + "_SWAP"));
    final ConventionBundle referenceRateConvention = conventionBundleSource.getConventionBundle(IdentifierBundle
        .of(conventionBundle.getSwapFloatingLegInitialRate()));
    final Identifier initialRefRateId = SecurityUtils.bloombergTickerSecurityId(referenceRateConvention
        .getIdentifiers().getIdentifier(SecurityUtils.BLOOMBERG_TICKER));
    result.add(new ValueRequirement(MarketDataRequirementNames.MARKET_VALUE, initialRefRateId));
    return Collections.unmodifiableSet(result);
  }

  @Override
  public String getShortName() {
    return "[" + _fundingCurveDefinitionName + ", " + _forwardCurveDefinitionName + ", " + _currency + "]"
        + "_MarketInstrumentImpliedYieldCurveFunction";
  }

  // ENG-252 This logic is wrong
  @SuppressWarnings("unused")
  private Instant findCurveExpiryDate(final SecuritySource securitySource,
      final InterpolatedYieldCurveSpecification specification, Instant expiry) {
    for (final FixedIncomeStripWithIdentifier strip : specification.getStrips()) {
      if (strip.getInstrumentType() == StripInstrumentType.FUTURE) {
        final FutureSecurity future = (FutureSecurity) securitySource.getSecurity(IdentifierBundle.of(strip
            .getSecurity()));
        final Instant futureInvalidAt = future.getExpiry().getExpiry().minus(strip.getMaturity().getPeriod())
            .toInstant();
        if (expiry == null) {
          expiry = futureInvalidAt;
        } else {
          if (futureInvalidAt.isBefore(expiry)) {
            expiry = futureInvalidAt;
          }
        }
      }
    }
    return expiry;
  }

  /**
   *
   */
  public final class Compiled extends AbstractFunction.AbstractInvokingCompiledFunction {

    private final InterpolatedYieldCurveSpecification _fundingCurveSpecification;
    private final Set<ValueRequirement> _fundingCurveRequirements;
    private final InterpolatedYieldCurveSpecification _forwardCurveSpecification;
    private final Set<ValueRequirement> _forwardCurveRequirements;
    private final Map<Identifier, Double> _identifierToFundingNodeTimes = new HashMap<Identifier, Double>();
    private final Map<Identifier, Double> _identifierToForwardNodeTimes = new HashMap<Identifier, Double>();

    private Compiled(final InstantProvider earliest, final InstantProvider latest,
        final InterpolatedYieldCurveSpecification fundingCurveSpecification,
        final Set<ValueRequirement> fundingCurveRequirements,
        final InterpolatedYieldCurveSpecification forwardCurveSpecification,
        final Set<ValueRequirement> forwardCurveRequirements) {
      super(earliest, latest);
      _fundingCurveSpecification = fundingCurveSpecification;
      _fundingCurveRequirements = fundingCurveRequirements;
      _forwardCurveSpecification = forwardCurveSpecification;
      _forwardCurveRequirements = forwardCurveRequirements;
    }

    public InterpolatedYieldCurveSpecification getFundingCurveSpecification() {
      return _fundingCurveSpecification;
    }

    public InterpolatedYieldCurveSpecification getForwardCurveSpecification() {
      return _forwardCurveSpecification;
    }

    public Map<Identifier, Double> getIdentifierToFundingNodeTimesMap() {
      return _identifierToFundingNodeTimes;
    }

    public Map<Identifier, Double> getIdentifierToForwardNodeTimesMap() {
      return _identifierToForwardNodeTimes;
    }

    @Override
    public Set<ComputedValue> execute(final FunctionExecutionContext executionContext, final FunctionInputs inputs,
        final ComputationTarget target, final Set<ValueRequirement> desiredValues) {
      final FixedIncomeStripIdentifierAndMaturityBuilder builder = new FixedIncomeStripIdentifierAndMaturityBuilder(
          OpenGammaExecutionContext.getRegionSource(executionContext),
          OpenGammaExecutionContext.getConventionBundleSource(executionContext), executionContext.getSecuritySource());
      final Clock snapshotClock = executionContext.getSnapshotClock();
      final ZonedDateTime now = snapshotClock.zonedDateTime();
      final HolidaySource holidaySource = OpenGammaExecutionContext.getHolidaySource(executionContext);
      final RegionSource regionSource = OpenGammaExecutionContext.getRegionSource(executionContext);
      final ExchangeSource exchangeSource = OpenGammaExecutionContext.getExchangeSource(executionContext);
      final ConventionBundleSource conventionSource = OpenGammaExecutionContext
          .getConventionBundleSource(executionContext);
      //final FixedFloatSwapSecurityToSwapConverter swapConverter = new FixedFloatSwapSecurityToSwapConverter(
      //    holidaySource, regionSource, conventionSource);
      final CashSecurityConverter cashConverter = new CashSecurityConverter(holidaySource, conventionSource);
      final FRASecurityConverter fraConverter = new FRASecurityConverter(holidaySource, conventionSource);
      final FutureSecurityConverter futureConverter = new FutureSecurityConverter(holidaySource, conventionSource,
          exchangeSource);
      final SwapSecurityConverter swapConverter = new SwapSecurityConverter(holidaySource, conventionSource,
          regionSource);
      final FinancialSecurityVisitorAdapter<FixedIncomeInstrumentDefinition<?>> instrumentAdapter = new FinancialSecurityVisitorAdapter<FixedIncomeInstrumentDefinition<?>>(
          null, cashConverter, null, fraConverter, null, null, swapConverter);
      final FinancialSecurityVisitorAdapter<FixedIncomeFutureInstrumentDefinition<?>> futureAdapter = new FinancialSecurityVisitorAdapter<FixedIncomeFutureInstrumentDefinition<?>>(
          null, null, null, null, futureConverter, null, null);
      final TenorSwapSecurityToTenorSwapConverter tenorSwapConverter = new TenorSwapSecurityToTenorSwapConverter(
          holidaySource, regionSource, conventionSource);
      final LocalDate localNow = now.toLocalDate();
      if (_fundingCurveDefinitionName.equals(_forwardCurveDefinitionName)) {
        // TODO going to arbitrarily use funding curve - will give the same result as forward curve
        final InterpolatedYieldCurveSpecificationWithSecurities specificationWithSecurities = builder
            .resolveToSecurity(_fundingCurveSpecification, buildMarketDataMap(inputs));
        final List<InterestRateDerivative> derivatives = new ArrayList<InterestRateDerivative>();
        final Set<FixedIncomeStrip> strips = _fundingCurveDefinition.getStrips();
        final int n = strips.size();
        final double[] initialRatesGuess = new double[n];
        final double[] nodeTimes = new double[n];
        final double[] parRates = new double[n];
        _identifierToFundingNodeTimes.clear();
        _identifierToForwardNodeTimes.clear();
        int i = 0;
        for (final FixedIncomeStripWithSecurity strip : specificationWithSecurities.getStrips()) {
          final ValueRequirement stripRequirement = new ValueRequirement(MarketDataRequirementNames.MARKET_VALUE,
              strip.getSecurityIdentifier());
          final Double marketValue = (Double) inputs.getValue(stripRequirement);
          if (marketValue == null) {
            throw new NullPointerException("Could not get market data for " + strip);
          }
          InterestRateDerivative derivative;
          final FinancialSecurity financialSecurity = (FinancialSecurity) strip.getSecurity();
          if (strip.getInstrumentType() == StripInstrumentType.SWAP) {
            derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow,
                _fundingCurveDefinitionName, _fundingCurveDefinitionName);
            //derivative = swapConverter.getSwap((SwapSecurity) strip.getSecurity(), _fundingCurveDefinitionName,
            //  _fundingCurveDefinitionName, marketValue / 100., 0.0, now);
          } else if (strip.getInstrumentType() == StripInstrumentType.CASH) {
            derivative = financialSecurity.accept(instrumentAdapter)
                .toDerivative(localNow, _fundingCurveDefinitionName);
          } else if (strip.getInstrumentType() == StripInstrumentType.FRA) {
            derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow,
                _fundingCurveDefinitionName, _fundingCurveDefinitionName);
          } else if (strip.getInstrumentType() == StripInstrumentType.FUTURE) {
            derivative = financialSecurity.accept(futureAdapter).toDerivative(localNow, marketValue,
                _fundingCurveDefinitionName);
          } else if (strip.getInstrumentType() == StripInstrumentType.LIBOR) {
            derivative = financialSecurity.accept(instrumentAdapter)
                .toDerivative(localNow, _fundingCurveDefinitionName);
          } else if (strip.getInstrumentType() == StripInstrumentType.TENOR_SWAP) {
            derivative = tenorSwapConverter.getSwap((SwapSecurity) strip.getSecurity(), _fundingCurveDefinitionName,
                _fundingCurveDefinitionName, _fundingCurveDefinitionName, marketValue / 10000., now);
          } else {
            throw new OpenGammaRuntimeException(
                "Can only handle swap, cash, LIBOR, FRA, IR futures and tenor swaps at the moment");
          }
          if (derivative == null) {
            throw new NullPointerException("Had a null InterestRateDefinition for " + strip);
          }
          if (strip.getInstrumentType() == StripInstrumentType.FUTURE) {
            parRates[i] = 1.0 - marketValue / 100;
          } else {
            parRates[i] = marketValue / 100.;
          }

          derivatives.add(derivative);
          initialRatesGuess[i] = 0.01;
          nodeTimes[i] = LAST_DATE_CALCULATOR.visit(derivative);
          _identifierToFundingNodeTimes.put(strip.getSecurityIdentifier(), nodeTimes[i]); // just for debugging.
          _identifierToForwardNodeTimes.put(strip.getSecurityIdentifier(), nodeTimes[i]); // just for debugging.
          i++;
        }
        ParallelArrayBinarySort.parallelBinarySort(nodeTimes, initialRatesGuess);
        final LinkedHashMap<String, double[]> curveKnots = new LinkedHashMap<String, double[]>();
        curveKnots.put(_fundingCurveDefinitionName, nodeTimes);
        final LinkedHashMap<String, double[]> curveNodes = new LinkedHashMap<String, double[]>();
        final LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>> interpolators = new LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>>();
        final LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>> sensitivityCalculators = new LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>>();
        final Interpolator1D<? extends Interpolator1DDataBundle> interpolator = CombinedInterpolatorExtrapolatorFactory
            .getInterpolator(_fundingCurveDefinition.getInterpolatorName(), Interpolator1DFactory.LINEAR_EXTRAPOLATOR,
                Interpolator1DFactory.FLAT_EXTRAPOLATOR);
        curveNodes.put(_fundingCurveDefinitionName, nodeTimes);
        interpolators.put(_fundingCurveDefinitionName, interpolator);
        // TODO have use finite difference or not as an input [FIN-147]
        final Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle> sensitivityCalculator = CombinedInterpolatorExtrapolatorNodeSensitivityCalculatorFactory
            .getSensitivityCalculator(_fundingCurveDefinition.getInterpolatorName(),
                Interpolator1DFactory.LINEAR_EXTRAPOLATOR, Interpolator1DFactory.FLAT_EXTRAPOLATOR, false);
        sensitivityCalculators.put(_fundingCurveDefinitionName, sensitivityCalculator);

        // TODO have the calculator and sensitivity calculators as an input [FIN-144], [FIN-145]
        // final MultipleYieldCurveFinderDataBundle data = new MultipleYieldCurveFinderDataBundle(derivatives, null, curveNodes, interpolators, sensitivityCalculators);
        // final Function1D<DoubleMatrix1D, DoubleMatrix1D> curveCalculator = new MultipleYieldCurveFinderFunction(data, PresentValueCalculator.getInstance());
        // final Function1D<DoubleMatrix1D, DoubleMatrix2D> jacobianCalculator = new MultipleYieldCurveFinderJacobian(data, PresentValueSensitivityCalculator.getInstance());
        // TODO check this ////////////////////////////////////////////////////////////////////////////////////////////////////////

        final MultipleYieldCurveFinderDataBundle data = new MultipleYieldCurveFinderDataBundle(derivatives, parRates,
            null, curveNodes, interpolators, sensitivityCalculators);
        final Function1D<DoubleMatrix1D, DoubleMatrix1D> curveCalculator = new MultipleYieldCurveFinderFunction(data,
            ParRateCalculator.getInstance());
        final Function1D<DoubleMatrix1D, DoubleMatrix2D> jacobianCalculator = new MultipleYieldCurveFinderJacobian(
            data, ParRateCurveSensitivityCalculator.getInstance());
        NewtonVectorRootFinder rootFinder;
        double[] yields = null;
        try {
          // TODO have the decomposition as an optional input [FIN-146]
          rootFinder = new BroydenVectorRootFinder(1e-7, 1e-7, 100,
              DecompositionFactory.getDecomposition(DecompositionFactory.LU_COMMONS_NAME));
          yields = rootFinder.getRoot(curveCalculator, jacobianCalculator, new DoubleMatrix1D(initialRatesGuess))
              .getData();
        } catch (final Exception e) {
          rootFinder = new BroydenVectorRootFinder(1e-7, 1e-7, 100,
              DecompositionFactory.getDecomposition(DecompositionFactory.SV_COMMONS_NAME));
          yields = rootFinder.getRoot(curveCalculator, jacobianCalculator, new DoubleMatrix1D(initialRatesGuess))
              .getData();

        }

        final YieldAndDiscountCurve fundingCurve = new YieldCurve(InterpolatedDoublesCurve.from(nodeTimes, yields,
            interpolator));
        final YieldAndDiscountCurve forwardCurve = new YieldCurve(InterpolatedDoublesCurve.from(nodeTimes, yields,
            interpolator));
        final DoubleMatrix2D jacobianMatrix = jacobianCalculator.evaluate(new DoubleMatrix1D(yields));
        return Sets.newHashSet(new ComputedValue(_fundingCurveResult, fundingCurve), new ComputedValue(
            _forwardCurveResult, forwardCurve), new ComputedValue(_jacobianResult, jacobianMatrix.getData()),
            new ComputedValue(_fundingCurveSpecResult, _fundingCurveSpecification), new ComputedValue(
                _forwardCurveSpecResult, _forwardCurveSpecification));
      }

      final InterpolatedYieldCurveSpecificationWithSecurities fundingCurveSpecificationWithSecurities = builder
          .resolveToSecurity(_fundingCurveSpecification, buildMarketDataMap(inputs));
      final InterpolatedYieldCurveSpecificationWithSecurities forwardCurveSpecificationWithSecurities = builder
          .resolveToSecurity(_forwardCurveSpecification, buildMarketDataMap(inputs));
      final List<InterestRateDerivative> derivatives = new ArrayList<InterestRateDerivative>();
      final Set<FixedIncomeStrip> fundingStrips = _fundingCurveDefinition.getStrips();
      final Set<FixedIncomeStrip> forwardStrips = _forwardCurveDefinition.getStrips();
      final int nFunding = fundingStrips.size();
      final int nForward = forwardStrips.size();
      final double[] initialRatesGuess = new double[nFunding + nForward];
      final double[] fundingNodeTimes = new double[nFunding];
      final double[] forwardNodeTimes = new double[nForward];
      final double[] parRates = new double[nFunding + nForward];
      _identifierToFundingNodeTimes.clear();
      _identifierToForwardNodeTimes.clear();
      int i = 0, fundingIndex = 0, forwardIndex = 0;
      for (final FixedIncomeStripWithSecurity strip : fundingCurveSpecificationWithSecurities.getStrips()) {
        final ValueRequirement stripRequirement = new ValueRequirement(MarketDataRequirementNames.MARKET_VALUE,
            strip.getSecurityIdentifier());
        final Double marketValue = (Double) inputs.getValue(stripRequirement);
        if (marketValue == null) {
          throw new NullPointerException("Could not get market data for " + strip);
        }
        final FinancialSecurity financialSecurity = (FinancialSecurity) strip.getSecurity();
        InterestRateDerivative derivative;
        if (strip.getInstrumentType() == StripInstrumentType.SWAP) {
          derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow, _fundingCurveDefinitionName,
              _forwardCurveDefinitionName);
          //derivative = swapConverter.getSwap((SwapSecurity) strip.getSecurity(), _fundingCurveDefinitionName,
          //  _forwardCurveDefinitionName, marketValue / 100., 0.0, now);
        } else if (strip.getInstrumentType() == StripInstrumentType.CASH) {
          derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow, _fundingCurveDefinitionName);
        } else if (strip.getInstrumentType() == StripInstrumentType.FRA) {
          derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow, _fundingCurveDefinitionName,
              _forwardCurveDefinitionName);
        } else if (strip.getInstrumentType() == StripInstrumentType.FUTURE) {
          derivative = financialSecurity.accept(futureAdapter).toDerivative(localNow, marketValue,
              _fundingCurveDefinitionName);
        } else if (strip.getInstrumentType() == StripInstrumentType.LIBOR) {
          derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow, _fundingCurveDefinitionName);
        } else if (strip.getInstrumentType() == StripInstrumentType.TENOR_SWAP) {
          derivative = tenorSwapConverter.getSwap((SwapSecurity) strip.getSecurity(), _fundingCurveDefinitionName,
              _forwardCurveDefinitionName, _fundingCurveDefinitionName, marketValue / 10000., now);
        } else {
          throw new OpenGammaRuntimeException(
              "Can only handle swap, cash, LIBOR, FRA, IR futures and tenor swaps at the moment");
        }
        if (derivative == null) {
          throw new NullPointerException("Had a null InterestRateDefinition for " + strip);
        }
        if (strip.getInstrumentType() == StripInstrumentType.FUTURE) {
          parRates[i] = 1.0 - marketValue / 100;
        } else {
          parRates[i] = marketValue / 100.;
        }
        derivatives.add(derivative);
        initialRatesGuess[i++] = 0.01;
        fundingNodeTimes[fundingIndex] = LAST_DATE_CALCULATOR.visit(derivative);
        _identifierToFundingNodeTimes.put(strip.getSecurityIdentifier(), fundingNodeTimes[fundingIndex]); // just for debugging.
        fundingIndex++;
      }
      for (final FixedIncomeStripWithSecurity strip : forwardCurveSpecificationWithSecurities.getStrips()) {
        final ValueRequirement stripRequirement = new ValueRequirement(MarketDataRequirementNames.MARKET_VALUE,
            strip.getSecurityIdentifier());
        final Double marketValue = (Double) inputs.getValue(stripRequirement);
        if (marketValue == null) {
          throw new NullPointerException("Could not get market data for " + strip);
        }
        InterestRateDerivative derivative;
        final FinancialSecurity financialSecurity = (FinancialSecurity) strip.getSecurity();
        if (strip.getInstrumentType() == StripInstrumentType.SWAP) {
          derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow, _fundingCurveDefinitionName,
              _forwardCurveDefinitionName);
          //derivative = swapConverter.getSwap((SwapSecurity) strip.getSecurity(), _fundingCurveDefinitionName,
          //  _forwardCurveDefinitionName, marketValue / 100., 0.0, now);
        } else if (strip.getInstrumentType() == StripInstrumentType.CASH) {
          derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow, _forwardCurveDefinitionName);
        } else if (strip.getInstrumentType() == StripInstrumentType.FRA) {
          derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow, _fundingCurveDefinitionName,
              _forwardCurveDefinitionName);
        } else if (strip.getInstrumentType() == StripInstrumentType.FUTURE) {
          derivative = financialSecurity.accept(futureAdapter).toDerivative(localNow, marketValue,
              _forwardCurveDefinitionName);
        } else if (strip.getInstrumentType() == StripInstrumentType.LIBOR) {
          derivative = financialSecurity.accept(instrumentAdapter).toDerivative(localNow, _forwardCurveDefinitionName);
        } else if (strip.getInstrumentType() == StripInstrumentType.TENOR_SWAP) {
          derivative = tenorSwapConverter.getSwap((SwapSecurity) strip.getSecurity(), _fundingCurveDefinitionName,
              _fundingCurveDefinitionName, _forwardCurveDefinitionName, marketValue / 10000., now);
        } else {
          throw new OpenGammaRuntimeException(
              "Can only handle swap, cash, LIBOR, FRA, IR futures and tenor swaps at the moment");
        }
        if (derivative == null) {
          throw new NullPointerException("Had a null InterestRateDefinition for " + strip);
        }
        derivatives.add(derivative);
        initialRatesGuess[i++] = 0.01;
        forwardNodeTimes[forwardIndex] = LAST_DATE_CALCULATOR.visit(derivative);
        _identifierToForwardNodeTimes.put(strip.getSecurityIdentifier(), forwardNodeTimes[forwardIndex]); // just for debugging.
        forwardIndex++;
      }
      Arrays.sort(fundingNodeTimes);
      Arrays.sort(forwardNodeTimes);
      // ParallelArrayBinarySort.parallelBinarySort(fundingNodeTimes, initialRatesGuess); //TODO will eventually need two sets of rates guesses
      // ParallelArrayBinarySort.parallelBinarySort(fundingNodeTimes, initialRatesGuess); //TODO will eventually need two sets of rates guesses
      final LinkedHashMap<String, double[]> curveKnots = new LinkedHashMap<String, double[]>();
      curveKnots.put(_fundingCurveDefinitionName, fundingNodeTimes);
      curveKnots.put(_forwardCurveDefinitionName, forwardNodeTimes);
      final LinkedHashMap<String, double[]> curveNodes = new LinkedHashMap<String, double[]>();
      final LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>> interpolators = new LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>>();
      final LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>> sensitivityCalculators = new LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>>();
      final Interpolator1D<? extends Interpolator1DDataBundle> fundingInterpolator = CombinedInterpolatorExtrapolatorFactory
          .getInterpolator(_fundingCurveDefinition.getInterpolatorName(), Interpolator1DFactory.LINEAR_EXTRAPOLATOR,
              Interpolator1DFactory.FLAT_EXTRAPOLATOR);
      final Interpolator1D<? extends Interpolator1DDataBundle> forwardInterpolator = CombinedInterpolatorExtrapolatorFactory
          .getInterpolator(_forwardCurveDefinition.getInterpolatorName(), Interpolator1DFactory.LINEAR_EXTRAPOLATOR,
              Interpolator1DFactory.FLAT_EXTRAPOLATOR);
      curveNodes.put(_fundingCurveDefinitionName, fundingNodeTimes);
      interpolators.put(_fundingCurveDefinitionName, fundingInterpolator);
      curveNodes.put(_forwardCurveDefinitionName, forwardNodeTimes);
      interpolators.put(_forwardCurveDefinitionName, forwardInterpolator);
      // TODO have use finite difference or not as an input [FIN-147]
      final Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle> fundingSensitivityCalculator = CombinedInterpolatorExtrapolatorNodeSensitivityCalculatorFactory
          .getSensitivityCalculator(_fundingCurveDefinition.getInterpolatorName(),
              Interpolator1DFactory.LINEAR_EXTRAPOLATOR, Interpolator1DFactory.FLAT_EXTRAPOLATOR, false);
      final Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle> forwardSensitivityCalculator = CombinedInterpolatorExtrapolatorNodeSensitivityCalculatorFactory
          .getSensitivityCalculator(_forwardCurveDefinition.getInterpolatorName(),
              Interpolator1DFactory.LINEAR_EXTRAPOLATOR, Interpolator1DFactory.FLAT_EXTRAPOLATOR, false);
      sensitivityCalculators.put(_fundingCurveDefinitionName, fundingSensitivityCalculator);
      sensitivityCalculators.put(_forwardCurveDefinitionName, forwardSensitivityCalculator);
      final MultipleYieldCurveFinderDataBundle data = new MultipleYieldCurveFinderDataBundle(derivatives, parRates,
          null, curveNodes, interpolators, sensitivityCalculators);
      // TODO have the calculator and sensitivity calculators as an input [FIN-144], [FIN-145]
      final Function1D<DoubleMatrix1D, DoubleMatrix1D> curveCalculator = new MultipleYieldCurveFinderFunction(data,
          PresentValueCalculator.getInstance());
      final Function1D<DoubleMatrix1D, DoubleMatrix2D> jacobianCalculator = new MultipleYieldCurveFinderJacobian(data,
          PresentValueSensitivityCalculator.getInstance());
      NewtonVectorRootFinder rootFinder;
      double[] yields = null;
      try {
        // TODO have the decomposition as an optional input [FIN-146]
        rootFinder = new BroydenVectorRootFinder(1e-7, 1e-7, 100,
            DecompositionFactory.getDecomposition(DecompositionFactory.LU_COMMONS_NAME));
        yields = rootFinder.getRoot(curveCalculator, jacobianCalculator, new DoubleMatrix1D(initialRatesGuess))
            .getData();
      } catch (final Exception e) {
        rootFinder = new BroydenVectorRootFinder(1e-7, 1e-7, 100,
            DecompositionFactory.getDecomposition(DecompositionFactory.SV_COMMONS_NAME));
        yields = rootFinder.getRoot(curveCalculator, jacobianCalculator, new DoubleMatrix1D(initialRatesGuess))
            .getData();

      }
      final double[] fundingYields = Arrays.copyOfRange(yields, 0, fundingNodeTimes.length);
      final double[] forwardYields = Arrays.copyOfRange(yields, fundingNodeTimes.length, yields.length);
      final YieldAndDiscountCurve fundingCurve = new YieldCurve(InterpolatedDoublesCurve.from(fundingNodeTimes,
          fundingYields, fundingInterpolator));
      final YieldAndDiscountCurve forwardCurve = new YieldCurve(InterpolatedDoublesCurve.from(forwardNodeTimes,
          forwardYields, forwardInterpolator));
      final DoubleMatrix2D jacobianMatrix = jacobianCalculator.evaluate(new DoubleMatrix1D(yields));
      return Sets.newHashSet(new ComputedValue(_fundingCurveResult, fundingCurve), new ComputedValue(
          _forwardCurveResult, forwardCurve), new ComputedValue(_jacobianResult, jacobianMatrix.getData()),
          new ComputedValue(_fundingCurveSpecResult, _fundingCurveSpecification), new ComputedValue(
              _forwardCurveSpecResult, _forwardCurveSpecification));

    }

    @Override
    public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
      if (target.getType() != ComputationTargetType.PRIMITIVE) {
        return false;
      }
      return ObjectUtils.equals(target.getUniqueId(), _fundingCurveSpecification.getCurrency().getUniqueId());
    }

    @Override
    public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context,
        final ComputationTarget target, final ValueRequirement desiredValue) {
      final Set<ValueRequirement> result = new HashSet<ValueRequirement>();
      result.addAll(_fundingCurveRequirements);
      result.addAll(_forwardCurveRequirements);
      return result;
    }

    @Override
    public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target) {
      return _results;
    }

    @Override
    public ComputationTargetType getTargetType() {
      return ComputationTargetType.PRIMITIVE;
    }

  }

  @Override
  public Compiled compile(final FunctionCompilationContext context, final InstantProvider atInstantProvider) {
    final ZonedDateTime atInstant = ZonedDateTime.ofInstant(atInstantProvider, TimeZone.UTC);
    final LocalDate curveDate = atInstant.toLocalDate();
    final InterpolatedYieldCurveSpecification fundingCurveSpecification = _curveSpecificationBuilder.buildCurve(
        curveDate, _fundingCurveDefinition);
    final InterpolatedYieldCurveSpecification forwardCurveSpecification = _curveSpecificationBuilder.buildCurve(
        curveDate, _forwardCurveDefinition);
    final Set<ValueRequirement> fundingCurveRequirements = buildRequirements(fundingCurveSpecification, context);
    final Set<ValueRequirement> forwardCurveRequirements = buildRequirements(forwardCurveSpecification, context);
    // ENG-252 expiry logic is wrong so make it valid for the current day only
    final Instant eod = atInstant.withTime(0, 0).plusDays(1).minusNanos(1000000).toInstant();
    Instant expiry = null;
    // expiry = findCurveExpiryDate(context.getSecuritySource(), fundingCurveSpecification, expiry);
    // expiry = findCurveExpiryDate(context.getSecuritySource(), forwardCurveSpecification, expiry);
    // if (expiry.isBefore(eod)) {
    expiry = eod;
    // }
    return new Compiled(atInstant.withTime(0, 0), expiry, fundingCurveSpecification, fundingCurveRequirements,
        forwardCurveSpecification, forwardCurveRequirements);
  }

  private Map<Identifier, Double> buildMarketDataMap(final FunctionInputs inputs) {
    final Map<Identifier, Double> marketDataMap = new HashMap<Identifier, Double>();
    for (final ComputedValue value : inputs.getAllValues()) {
      final ComputationTargetSpecification targetSpecification = value.getSpecification().getTargetSpecification();
      if (value.getValue() instanceof Double) {
        marketDataMap.put(targetSpecification.getIdentifier(), (Double) value.getValue());
      }
    }
    return marketDataMap;
  }

}

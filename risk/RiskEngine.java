package risk;

import finance.PnLCalculator;
import finance.Portfolio;
import finance.PortfolioPricer;
import model.MarketState;
import model.SimulationResult;
import model.SimulationScenario;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Coordinates portfolio risk analysis from simulated
 * market scenarios.
 *
 * <p>The engine computes the P&L distribution, converts it
 * into a loss distribution, and evaluates Value at Risk and
 * Expected Shortfall for the requested confidence levels.</p>
 */
public final class RiskEngine {

    private final PortfolioPricer portfolioPricer;
    private final PnLCalculator pnlCalculator;
    private final RiskMeasure valueAtRisk;
    private final RiskMeasure expectedShortfall;

    /**
     * Creates a risk engine.
     *
     * @param portfolioPricer portfolio valuation service
     * @param pnlCalculator P&L calculation service
     * @param valueAtRisk Value at Risk implementation
     * @param expectedShortfall Expected Shortfall implementation
     */
    public RiskEngine(
            PortfolioPricer portfolioPricer,
            PnLCalculator pnlCalculator,
            RiskMeasure valueAtRisk,
            RiskMeasure expectedShortfall) {

        this.portfolioPricer =
                Objects.requireNonNull(
                        portfolioPricer,
                        "portfolioPricer must not be null"
                );

        this.pnlCalculator =
                Objects.requireNonNull(
                        pnlCalculator,
                        "pnlCalculator must not be null"
                );

        this.valueAtRisk =
                Objects.requireNonNull(
                        valueAtRisk,
                        "valueAtRisk must not be null"
                );

        this.expectedShortfall =
                Objects.requireNonNull(
                        expectedShortfall,
                        "expectedShortfall must not be null"
                );
    }

    /**
     * Performs the full portfolio risk analysis.
     *
     * @param portfolio portfolio being analyzed
     * @param initialState initial market state
     * @param scenarios simulated future market scenarios
     * @param confidenceLevels requested confidence levels
     * @return complete simulation result
     */
    public SimulationResult analyze(
            Portfolio portfolio,
            MarketState initialState,
            List<SimulationScenario> scenarios,
            double[] confidenceLevels) {

        validateInputs(
                portfolio,
                initialState,
                scenarios,
                confidenceLevels
        );

        double initialPortfolioValue =
                portfolioPricer.value(
                        portfolio,
                        initialState
                );

        double[] pnlDistribution =
                pnlCalculator.calculateDistribution(
                        portfolio,
                        initialState,
                        scenarios
                );

        double[] lossDistribution =
                convertPnLToLoss(
                        pnlDistribution
                );

        Map<Double, Double> varResults =
                new LinkedHashMap<>();

        Map<Double, Double> esResults =
                new LinkedHashMap<>();

        for (double confidenceLevel
                : confidenceLevels) {

            validateConfidenceLevel(
                    confidenceLevel
            );

            double var =
                    valueAtRisk.compute(
                            lossDistribution,
                            confidenceLevel
                    );

            double es =
                    expectedShortfall.compute(
                            lossDistribution,
                            confidenceLevel
                    );

            varResults.put(
                    confidenceLevel,
                    var
            );

            esResults.put(
                    confidenceLevel,
                    es
            );
        }

        return new SimulationResult(
                initialPortfolioValue,
                pnlDistribution,
                varResults,
                esResults
        );
    }

    /**
     * Converts P&L values into losses.
     *
     * <pre>
     * loss = -PnL
     * </pre>
     */
    private static double[] convertPnLToLoss(
            double[] pnlDistribution) {

        double[] lossDistribution =
                new double[pnlDistribution.length];

        for (int i = 0;
             i < pnlDistribution.length;
             i++) {

            lossDistribution[i] =
                    -pnlDistribution[i];
        }

        return lossDistribution;
    }

    private static void validateInputs(
            Portfolio portfolio,
            MarketState initialState,
            List<SimulationScenario> scenarios,
            double[] confidenceLevels) {

        Objects.requireNonNull(
                portfolio,
                "portfolio must not be null"
        );

        Objects.requireNonNull(
                initialState,
                "initialState must not be null"
        );

        Objects.requireNonNull(
                scenarios,
                "scenarios must not be null"
        );

        if (scenarios.isEmpty()) {
            throw new IllegalArgumentException(
                    "scenarios must not be empty"
            );
        }

        Objects.requireNonNull(
                confidenceLevels,
                "confidenceLevels must not be null"
        );

        if (confidenceLevels.length == 0) {
            throw new IllegalArgumentException(
                    "confidenceLevels must not be empty"
            );
        }
    }

    private static void validateConfidenceLevel(
            double confidenceLevel) {

        if (!Double.isFinite(confidenceLevel)
                || confidenceLevel <= 0.0
                || confidenceLevel >= 1.0) {

            throw new IllegalArgumentException(
                    "confidenceLevel must be "
                    + "strictly between 0 and 1"
            );
        }
    }
}
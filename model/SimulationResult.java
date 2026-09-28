package model;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the final output of a portfolio risk simulation.
 *
 * <p>The result contains the initial portfolio value,
 * the Monte Carlo P&L distribution and the risk measures
 * calculated for each requested confidence level.</p>
 *
 * <p>The class is immutable.</p>
 */
public final class SimulationResult {

    private final double initialPortfolioValue;
    private final double[] pnlDistribution;

    private final Map<Double, Double> valueAtRisk;
    private final Map<Double, Double> expectedShortfall;

    /**
     * Creates a simulation result.
     *
     * @param initialPortfolioValue initial value of the portfolio
     * @param pnlDistribution simulated P&L distribution
     * @param valueAtRisk VaR values indexed by confidence level
     * @param expectedShortfall ES values indexed by confidence level
     */
    public SimulationResult(
            double initialPortfolioValue,
            double[] pnlDistribution,
            Map<Double, Double> valueAtRisk,
            Map<Double, Double> expectedShortfall) {

        if (!Double.isFinite(initialPortfolioValue)) {
            throw new IllegalArgumentException(
                    "initialPortfolioValue must be finite"
            );
        }

        Objects.requireNonNull(
                pnlDistribution,
                "pnlDistribution must not be null"
        );

        if (pnlDistribution.length == 0) {
            throw new IllegalArgumentException(
                    "pnlDistribution must not be empty"
            );
        }

        this.pnlDistribution =
                pnlDistribution.clone();

        for (double pnl : this.pnlDistribution) {

            if (!Double.isFinite(pnl)) {
                throw new IllegalArgumentException(
                        "PnL values must be finite"
                );
            }
        }

        this.valueAtRisk =
                validateAndCopyRiskMeasures(
                        valueAtRisk,
                        "valueAtRisk"
                );

        this.expectedShortfall =
                validateAndCopyRiskMeasures(
                        expectedShortfall,
                        "expectedShortfall"
                );

        if (!this.valueAtRisk.keySet()
                .equals(
                        this.expectedShortfall.keySet()
                )) {

            throw new IllegalArgumentException(
                    "VaR and Expected Shortfall must "
                    + "use the same confidence levels"
            );
        }

        this.initialPortfolioValue =
                initialPortfolioValue;
    }

    public double getInitialPortfolioValue() {
        return initialPortfolioValue;
    }

    /**
     * Returns a defensive copy of the P&L distribution.
     *
     * @return simulated P&L values
     */
    public double[] getPnlDistribution() {
        return pnlDistribution.clone();
    }

    public Map<Double, Double> getValueAtRisk() {
        return valueAtRisk;
    }

    public Map<Double, Double> getExpectedShortfall() {
        return expectedShortfall;
    }

    /**
     * Returns VaR for a confidence level.
     *
     * @param confidenceLevel confidence level in (0, 1)
     * @return VaR value
     */
    public double getValueAtRisk(
            double confidenceLevel) {

        Double value =
                valueAtRisk.get(confidenceLevel);

        if (value == null) {
            throw new IllegalArgumentException(
                    "No VaR result for confidence level: "
                    + confidenceLevel
            );
        }

        return value;
    }

    /**
     * Returns Expected Shortfall for a confidence level.
     *
     * @param confidenceLevel confidence level in (0, 1)
     * @return Expected Shortfall
     */
    public double getExpectedShortfall(
            double confidenceLevel) {

        Double value =
                expectedShortfall.get(
                        confidenceLevel
                );

        if (value == null) {
            throw new IllegalArgumentException(
                    "No Expected Shortfall result "
                    + "for confidence level: "
                    + confidenceLevel
            );
        }

        return value;
    }

    public int getNumberOfScenarios() {
        return pnlDistribution.length;
    }

    private static Map<Double, Double>
    validateAndCopyRiskMeasures(
            Map<Double, Double> measures,
            String name) {

        Objects.requireNonNull(
                measures,
                name + " must not be null"
        );

        if (measures.isEmpty()) {
            throw new IllegalArgumentException(
                    name + " must not be empty"
            );
        }

        Map<Double, Double> copy =
                new LinkedHashMap<>();

        for (Map.Entry<Double, Double> entry
                : measures.entrySet()) {

            Double confidence =
                    Objects.requireNonNull(
                            entry.getKey(),
                            "confidence level must not be null"
                    );

            Double value =
                    Objects.requireNonNull(
                            entry.getValue(),
                            "risk measure must not be null"
                    );

            if (!Double.isFinite(confidence)
                    || confidence <= 0.0
                    || confidence >= 1.0) {

                throw new IllegalArgumentException(
                        "confidence level must be "
                        + "strictly between 0 and 1"
                );
            }

            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException(
                        "risk measure values must be finite"
                );
            }

            copy.put(confidence, value);
        }

        return Map.copyOf(copy);
    }

    @Override
    public String toString() {
        return "SimulationResult{" +
                "initialPortfolioValue="
                + initialPortfolioValue +
                ", numberOfScenarios="
                + pnlDistribution.length +
                ", valueAtRisk="
                + valueAtRisk +
                ", expectedShortfall="
                + expectedShortfall +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SimulationResult other)) {
            return false;
        }

        return Double.compare(
                initialPortfolioValue,
                other.initialPortfolioValue
        ) == 0
                && Arrays.equals(
                        pnlDistribution,
                        other.pnlDistribution
                )
                && valueAtRisk.equals(
                        other.valueAtRisk
                )
                && expectedShortfall.equals(
                        other.expectedShortfall
                );
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(
                initialPortfolioValue,
                valueAtRisk,
                expectedShortfall
        );

        result =
                31 * result
                        + Arrays.hashCode(
                                pnlDistribution
                        );

        return result;
    }
}

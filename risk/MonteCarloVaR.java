package risk;

import java.util.Arrays;
import java.util.Objects;

/**
 * Computes Value at Risk from an empirical Monte Carlo
 * loss distribution.
 *
 * <p>The empirical quantile is defined using the order statistic:</p>
 *
 * <pre>
 * k = ceil(alpha * n)
 * VaR_alpha = sortedLosses[k - 1]
 * </pre>
 *
 * where losses are sorted in ascending order.
 */
public final class MonteCarloVaR implements RiskMeasure {

    /**
     * Computes Value at Risk for an empirical loss distribution.
     *
     * @param lossDistribution simulated loss distribution
     * @param confidenceLevel confidence level strictly between 0 and 1
     * @return Value at Risk
     */
    @Override
    public double compute(
            double[] lossDistribution,
            double confidenceLevel) {

        validateInputs(
                lossDistribution,
                confidenceLevel
        );

        double[] sortedLosses =
                lossDistribution.clone();

        Arrays.sort(sortedLosses);

        int n = sortedLosses.length;

        int index =
                (int) Math.ceil(
                        confidenceLevel * n
                ) - 1;

        return sortedLosses[index];
    }

    /**
     * Validates the input loss distribution
     * and confidence level.
     */
    private static void validateInputs(
            double[] lossDistribution,
            double confidenceLevel) {

        Objects.requireNonNull(
                lossDistribution,
                "lossDistribution must not be null"
        );

        if (lossDistribution.length == 0) {
            throw new IllegalArgumentException(
                    "lossDistribution must not be empty"
            );
        }

        for (double loss : lossDistribution) {

            if (!Double.isFinite(loss)) {
                throw new IllegalArgumentException(
                        "lossDistribution must contain "
                        + "only finite values"
                );
            }
        }

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
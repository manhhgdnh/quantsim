package risk;

import java.util.Arrays;
import java.util.Objects;

/**
 * Computes Expected Shortfall from an empirical Monte Carlo
 * loss distribution.
 *
 * <p>Expected Shortfall at confidence level alpha corresponds
 * to the average loss in the worst (1 - alpha) fraction of
 * simulated scenarios.</p>
 *
 * <p>When the empirical tail size is not an integer,
 * fractional weighting is applied to the boundary observation.</p>
 */
public final class ExpectedShortfall implements RiskMeasure {

    /**
     * Computes Expected Shortfall for an empirical
     * loss distribution.
     *
     * @param lossDistribution simulated loss distribution
     * @param confidenceLevel confidence level strictly between 0 and 1
     * @return Expected Shortfall
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

        double quantilePosition =
                confidenceLevel * n;

        int firstTailIndex =
                (int) Math.floor(quantilePosition);

        double fractionalWeight =
                firstTailIndex + 1
                        - quantilePosition;

        double tailSum = 0.0;

        /*
         * If quantilePosition is not an integer,
         * the boundary observation contributes only
         * partially to the tail.
         */
        if (fractionalWeight < 1.0
                && firstTailIndex < n) {

            tailSum += fractionalWeight
                    * sortedLosses[firstTailIndex];

            firstTailIndex++;
        }

        /*
         * Add all observations strictly inside
         * the worst tail.
         */
        for (int i = firstTailIndex; i < n; i++) {
            tailSum += sortedLosses[i];
        }

        double tailSize =
                n * (1.0 - confidenceLevel);

        return tailSum / tailSize;
    }

    /**
     * Validates input parameters.
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

package risk;

/**
 * Represents a statistical risk measure computed
 * from a loss distribution.
 *
 * <p>Positive values represent losses, while negative
 * values represent gains.</p>
 */
public interface RiskMeasure {

    /**
     * Computes the risk measure for the given empirical
     * loss distribution and confidence level.
     *
     * @param lossDistribution empirical loss distribution
     * @param confidenceLevel confidence level strictly between 0 and 1
     * @return risk measure value
     *
     * @throws NullPointerException if lossDistribution is null
     * @throws IllegalArgumentException if the distribution is empty,
     *                                  contains non-finite values,
     *                                  or if the confidence level is invalid
     */
    double compute(
            double[] lossDistribution,
            double confidenceLevel);
}

package finance;

import model.MarketState;

import java.util.Objects;

/**
 * Computes the market value of a portfolio
 * under a given market state.
 */
public final class PortfolioPricer {

    /**
     * Computes the total value of a portfolio.
     *
     * <p>The portfolio value is defined as:</p>
     *
     * <pre>
     * V = sum(quantity_i * instrumentValue_i)
     * </pre>
     *
     * @param portfolio portfolio to value
     * @param marketState market state used for valuation
     * @return total portfolio value
     *
     * @throws NullPointerException if portfolio or marketState is null
     */
    public double value(
            Portfolio portfolio,
            MarketState marketState) {

        Objects.requireNonNull(
                portfolio,
                "portfolio must not be null"
        );

        Objects.requireNonNull(
                marketState,
                "marketState must not be null"
        );

        double totalValue = 0.0;

        for (Position position : portfolio.getPositions()) {
            totalValue += position.value(marketState);
        }

        return totalValue;
    }
}
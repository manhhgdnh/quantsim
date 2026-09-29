package risk;

import finance.Portfolio;
import finance.PortfolioPricer;
import model.MarketState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Applies deterministic relative market shocks
 * and evaluates their impact on a portfolio.
 *
 * <p>A relative shock s is applied as:</p>
 *
 * <pre>
 * stressedPrice = initialPrice * (1 + s)
 * </pre>
 *
 * <p>For example, a shock of -0.20 represents
 * a 20% decrease in price.</p>
 */
public final class StressTest {

    private final PortfolioPricer portfolioPricer;

    /**
     * Creates a stress testing service.
     *
     * @param portfolioPricer portfolio valuation service
     */
    public StressTest(PortfolioPricer portfolioPricer) {

        this.portfolioPricer =
                Objects.requireNonNull(
                        portfolioPricer,
                        "portfolioPricer must not be null"
                );
    }

    /**
     * Applies relative shocks to a market state.
     *
     * <p>Assets without an explicitly specified shock
     * keep their original market price.</p>
     *
     * @param baseState original market state
     * @param relativeShocks relative shocks indexed by symbol
     * @return stressed market state
     */
    public MarketState applyShocks(
            MarketState baseState,
            Map<String, Double> relativeShocks) {

        Objects.requireNonNull(
                baseState,
                "baseState must not be null"
        );

        Objects.requireNonNull(
                relativeShocks,
                "relativeShocks must not be null"
        );

        Map<String, Double> stressedPrices =
                new LinkedHashMap<>();

        for (String symbol : baseState.getSymbols()) {

            double initialPrice =
                    baseState.getPrice(symbol);

            double shock =
                    relativeShocks.getOrDefault(
                            symbol,
                            0.0
                    );

            validateShock(
                    symbol,
                    shock
            );

            double stressedPrice =
                    initialPrice * (1.0 + shock);

            stressedPrices.put(
                    symbol,
                    stressedPrice
            );
        }

        validateShockSymbols(
                baseState,
                relativeShocks
        );

        return new MarketState(
                baseState.getTime(),
                stressedPrices
        );
    }

    /**
     * Computes the portfolio value after applying
     * the specified market shocks.
     *
     * @param portfolio portfolio being stressed
     * @param baseState original market state
     * @param relativeShocks relative market shocks
     * @return stressed portfolio value
     */
    public double computeStressedValue(
            Portfolio portfolio,
            MarketState baseState,
            Map<String, Double> relativeShocks) {

        Objects.requireNonNull(
                portfolio,
                "portfolio must not be null"
        );

        MarketState stressedState =
                applyShocks(
                        baseState,
                        relativeShocks
                );

        return portfolioPricer.value(
                portfolio,
                stressedState
        );
    }

    /**
     * Computes the portfolio P&L caused by the stress scenario.
     *
     * <pre>
     * stressPnL = stressedValue - initialValue
     * </pre>
     *
     * @param portfolio portfolio being stressed
     * @param baseState original market state
     * @param relativeShocks relative market shocks
     * @return stress P&L
     */
    public double computePnL(
            Portfolio portfolio,
            MarketState baseState,
            Map<String, Double> relativeShocks) {

        Objects.requireNonNull(
                portfolio,
                "portfolio must not be null"
        );

        double initialValue =
                portfolioPricer.value(
                        portfolio,
                        baseState
                );

        double stressedValue =
                computeStressedValue(
                        portfolio,
                        baseState,
                        relativeShocks
                );

        return stressedValue - initialValue;
    }

    private static void validateShock(
            String symbol,
            double shock) {

        if (!Double.isFinite(shock)) {
            throw new IllegalArgumentException(
                    "Shock must be finite for "
                    + symbol
            );
        }

        if (shock < -1.0) {
            throw new IllegalArgumentException(
                    "Shock must be >= -1.0 for "
                    + symbol
            );
        }
    }

    private static void validateShockSymbols(
            MarketState baseState,
            Map<String, Double> relativeShocks) {

        for (Map.Entry<String, Double> entry
                : relativeShocks.entrySet()) {

            String symbol =
                    Objects.requireNonNull(
                            entry.getKey(),
                            "shock symbol must not be null"
                    );

            Objects.requireNonNull(
                    entry.getValue(),
                    "shock value must not be null"
            );

            if (!baseState.containsSymbol(symbol)) {
                throw new IllegalArgumentException(
                        "Unknown asset symbol in stress scenario: "
                        + symbol
                );
            }

            validateShock(
                    symbol,
                    entry.getValue()
            );
        }
    }
}

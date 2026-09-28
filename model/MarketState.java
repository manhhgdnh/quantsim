package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable snapshot of market prices at a given simulation time.
 *
 * <p>A MarketState associates each financial instrument symbol
 * with its market price at a given time.</p>
 *
 * <p>The object is immutable and therefore safe to share
 * between different modules or threads.</p>
 */

public final class MarketState {

    private final double time;
    private final Map<String, Double> prices;

    /**
     * Creates a market state.
     *
     * @param time   simulation time, expressed in years
     * @param prices mapping between asset symbols and prices
     *
     * @throws IllegalArgumentException if time is invalid,
     *                                  if prices is empty,
     *                                  or if a price is invalid
     * @throws NullPointerException if prices or one of its entries is null
     */
    public MarketState(double time, Map<String, Double> prices) {

        validateTime(time);

        Objects.requireNonNull(prices, "prices must not be null");

        if (prices.isEmpty()) {
            throw new IllegalArgumentException(
                    "prices must contain at least one asset"
            );
        }

        Map<String, Double> copy = new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry : prices.entrySet()) {

            String symbol = Objects.requireNonNull(
                    entry.getKey(),
                    "asset symbol must not be null"
            );

            Double price = Objects.requireNonNull(
                    entry.getValue(),
                    "asset price must not be null"
            );

            validateSymbol(symbol);
            validatePrice(price);

            copy.put(symbol, price);
        }

        this.time = time;
        this.prices = Collections.unmodifiableMap(copy);
    }

    /**
     * Returns the simulation time represented by this state.
     *
     * @return time expressed in years
     */
    public double getTime() {
        return time;
    }

    /**
     * Returns the market price associated with a symbol.
     *
     * @param symbol asset symbol
     * @return asset price
     *
     * @throws IllegalArgumentException if the symbol
     *                                  does not exist in this market state
     */
    public double getPrice(String symbol) {

        Objects.requireNonNull(symbol, "symbol must not be null");

        Double price = prices.get(symbol);

        if (price == null) {
            throw new IllegalArgumentException(
                    "Unknown asset symbol: " + symbol
            );
        }

        return price;
    }

    /**
     * Returns whether this market state contains the given symbol.
     *
     * @param symbol asset symbol
     * @return true if the symbol exists
     */
    public boolean containsSymbol(String symbol) {
        return prices.containsKey(symbol);
    }

    /**
     * Returns all asset symbols contained in this market state.
     *
     * @return an unmodifiable set of symbols
     */
    public Set<String> getSymbols() {
        return prices.keySet();
    }

    /**
     * Returns an unmodifiable view of the asset prices.
     *
     * @return asset prices
     */
    public Map<String, Double> getPrices() {
        return prices;
    }

    private static void validateTime(double time) {

        if (!Double.isFinite(time) || time < 0.0) {
            throw new IllegalArgumentException(
                    "time must be finite and >= 0"
            );
        }
    }

    private static void validateSymbol(String symbol) {

        if (symbol.isBlank()) {
            throw new IllegalArgumentException(
                    "asset symbol must not be blank"
            );
        }
    }

    private static void validatePrice(double price) {

        if (!Double.isFinite(price) || price < 0.0) {
            throw new IllegalArgumentException(
                    "asset price must be finite and >= 0"
            );
        }
    }

    @Override
    public String toString() {
        return "MarketState{" +
                "time=" + time +
                ", prices=" + prices +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MarketState other)) {
            return false;
        }

        return Double.compare(time, other.time) == 0
                && prices.equals(other.prices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, prices);
    }
}
package finance;

import model.MarketState;

import java.util.Objects;

/**
 * Represents a stock identified by its market symbol.
 *
 * <p>A Stock does not store its market price. Its value is
 * obtained from a {@link MarketState}.</p>
 *
 * <p>The class is immutable.</p>
 */
public final class Stock implements FinancialInstrument {

    private final String symbol;

    /**
     * Creates a stock.
     *
     * @param symbol stock market symbol
     * @throws NullPointerException if symbol is null
     * @throws IllegalArgumentException if symbol is blank
     */
    public Stock(String symbol) {

        Objects.requireNonNull(
                symbol,
                "symbol must not be null"
        );

        String normalizedSymbol = symbol.trim();

        if (normalizedSymbol.isEmpty()) {
            throw new IllegalArgumentException(
                    "symbol must not be blank"
            );
        }

        this.symbol = normalizedSymbol;
    }

    /**
     * Returns the stock symbol.
     *
     * @return stock symbol
     */
    @Override
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the market price of one share of this stock
     * under the given market state.
     *
     * @param marketState market state containing asset prices
     * @return price of one share
     *
     * @throws NullPointerException if marketState is null
     * @throws IllegalArgumentException if the stock symbol
     *                                  is not present in the market state
     */
    @Override
    public double value(MarketState marketState) {

        Objects.requireNonNull(
                marketState,
                "marketState must not be null"
        );

        return marketState.getPrice(symbol);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Stock other)) {
            return false;
        }

        return symbol.equals(other.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                '}';
    }
}
package finance;

import model.MarketState;

import java.util.Objects;

/**
 * Represents a position in a financial instrument.
 *
 * <p>A position associates a financial instrument with a quantity.
 * Positive quantities represent long positions, while negative
 * quantities represent short positions.</p>
 *
 * <p>The class is immutable.</p>
 */
public final class Position {

    private final FinancialInstrument instrument;
    private final double quantity;

    /**
     * Creates a financial position.
     *
     * @param instrument financial instrument held by the position
     * @param quantity quantity of the instrument;
     *                 positive for long positions and negative for short positions
     *
     * @throws NullPointerException if instrument is null
     * @throws IllegalArgumentException if quantity is zero,
     *                                  NaN or infinite
     */
    public Position(
            FinancialInstrument instrument,
            double quantity) {

        this.instrument = Objects.requireNonNull(
                instrument,
                "instrument must not be null"
        );

        if (!Double.isFinite(quantity)) {
            throw new IllegalArgumentException(
                    "quantity must be finite"
            );
        }

        if (quantity == 0.0) {
            throw new IllegalArgumentException(
                    "quantity must not be zero"
            );
        }

        this.quantity = quantity;
    }

    /**
     * Returns the financial instrument held by this position.
     *
     * @return financial instrument
     */
    public FinancialInstrument getInstrument() {
        return instrument;
    }

    /**
     * Returns the quantity of the instrument.
     *
     * @return position quantity
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Computes the market value of the position.
     *
     * @param marketState market state used for valuation
     * @return position market value
     */
    public double value(MarketState marketState) {

        Objects.requireNonNull(
                marketState,
                "marketState must not be null"
        );

        return quantity * instrument.value(marketState);
    }

    /**
     * Indicates whether this is a long position.
     *
     * @return true if quantity is positive
     */
    public boolean isLong() {
        return quantity > 0.0;
    }

    /**
     * Indicates whether this is a short position.
     *
     * @return true if quantity is negative
     */
    public boolean isShort() {
        return quantity < 0.0;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Position other)) {
            return false;
        }

        return Double.compare(quantity, other.quantity) == 0
                && instrument.equals(other.instrument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument, quantity);
    }

    @Override
    public String toString() {
        return "Position{" +
                "instrument=" + instrument +
                ", quantity=" + quantity +
                '}';
    }
}
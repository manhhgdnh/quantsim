package finance;

import java.util.List;
import java.util.Objects;

/**
 * Represents an immutable portfolio composed of financial positions.
 *
 * <p>Multiple positions referencing the same financial instrument
 * are allowed.</p>
 */
public final class Portfolio {

    private final List<Position> positions;

    /**
     * Creates a portfolio.
     *
     * @param positions portfolio positions
     * @throws NullPointerException if positions or one element is null
     * @throws IllegalArgumentException if positions is empty
     */
    public Portfolio(List<Position> positions) {

        Objects.requireNonNull(
                positions,
                "positions must not be null"
        );

        if (positions.isEmpty()) {
            throw new IllegalArgumentException(
                    "portfolio must contain at least one position"
            );
        }

        this.positions = List.copyOf(positions);
    }

    /**
     * Returns the portfolio positions.
     *
     * @return unmodifiable list of positions
     */
    public List<Position> getPositions() {
        return positions;
    }

    /**
     * Returns the number of positions.
     *
     * @return number of positions
     */
    public int size() {
        return positions.size();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Portfolio other)) {
            return false;
        }

        return positions.equals(other.positions);
    }

    @Override
    public int hashCode() {
        return positions.hashCode();
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "positions=" + positions +
                '}';
    }
}
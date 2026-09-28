package finance;

import model.MarketState;

/**
 * Represents a financial instrument that can be valued
 * from a given market state.
 *
 * <p>Implementations define how the instrument value
 * is obtained from market data.</p>
 */
public interface FinancialInstrument {

    /**
     * Returns the unique symbol identifying the instrument.
     *
     * @return instrument symbol
     */
    String getSymbol();

    /**
     * Computes the value of one unit of the instrument
     * under the given market state.
     *
     * @param marketState market data used for valuation
     * @return value of one unit of the instrument
     *
     * @throws NullPointerException if marketState is null
     * @throws IllegalArgumentException if required market data
     *                                  is missing
     */
    double value(MarketState marketState);
}
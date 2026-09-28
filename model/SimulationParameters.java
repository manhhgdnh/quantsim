package model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable configuration used by the Monte Carlo simulation engine.
 *
 * <p>The parameters include the simulation horizon, time discretization,
 * number of Monte Carlo paths, random seed, number of worker threads,
 * and stochastic parameters for each simulated asset.</p>
 */
public final class SimulationParameters {

    private final double horizon;
    private final int numberOfSteps;
    private final int numberOfPaths;
    private final long seed;
    private final int numberOfThreads;

    private final Map<String, Double> drifts;
    private final Map<String, Double> volatilities;

    /**
     * Creates simulation parameters.
     *
     * @param horizon simulation horizon expressed in years
     * @param numberOfSteps number of time steps per simulation path
     * @param numberOfPaths number of Monte Carlo paths
     * @param seed random number generator seed
     * @param numberOfThreads number of worker threads
     * @param drifts drift parameter for each asset
     * @param volatilities volatility parameter for each asset
     */
    public SimulationParameters(
            double horizon,
            int numberOfSteps,
            int numberOfPaths,
            long seed,
            int numberOfThreads,
            Map<String, Double> drifts,
            Map<String, Double> volatilities) {

        validateGeneralParameters(
                horizon,
                numberOfSteps,
                numberOfPaths,
                numberOfThreads
        );

        this.drifts = validateAndCopyDrifts(drifts);
        this.volatilities =
                validateAndCopyVolatilities(volatilities);

        if (!this.drifts.keySet()
                .equals(this.volatilities.keySet())) {

            throw new IllegalArgumentException(
                    "drifts and volatilities must contain "
                    + "the same asset symbols"
            );
        }

        this.horizon = horizon;
        this.numberOfSteps = numberOfSteps;
        this.numberOfPaths = numberOfPaths;
        this.seed = seed;
        this.numberOfThreads = numberOfThreads;
    }

    public double getHorizon() {
        return horizon;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public int getNumberOfPaths() {
        return numberOfPaths;
    }

    public long getSeed() {
        return seed;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public Map<String, Double> getDrifts() {
        return drifts;
    }

    public Map<String, Double> getVolatilities() {
        return volatilities;
    }

    /**
     * Returns the drift associated with an asset.
     *
     * @param symbol asset symbol
     * @return asset drift
     */
    public double getDrift(String symbol) {

        Objects.requireNonNull(
                symbol,
                "symbol must not be null"
        );

        Double drift = drifts.get(symbol);

        if (drift == null) {
            throw new IllegalArgumentException(
                    "Unknown asset symbol: " + symbol
            );
        }

        return drift;
    }

    /**
     * Returns the volatility associated with an asset.
     *
     * @param symbol asset symbol
     * @return asset volatility
     */
    public double getVolatility(String symbol) {

        Objects.requireNonNull(
                symbol,
                "symbol must not be null"
        );

        Double volatility =
                volatilities.get(symbol);

        if (volatility == null) {
            throw new IllegalArgumentException(
                    "Unknown asset symbol: " + symbol
            );
        }

        return volatility;
    }

    public Set<String> getSymbols() {
        return drifts.keySet();
    }

    private static void validateGeneralParameters(
            double horizon,
            int numberOfSteps,
            int numberOfPaths,
            int numberOfThreads) {

        if (!Double.isFinite(horizon)
                || horizon <= 0.0) {

            throw new IllegalArgumentException(
                    "horizon must be finite and > 0"
            );
        }

        if (numberOfSteps <= 0) {
            throw new IllegalArgumentException(
                    "numberOfSteps must be > 0"
            );
        }

        if (numberOfPaths <= 0) {
            throw new IllegalArgumentException(
                    "numberOfPaths must be > 0"
            );
        }

        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException(
                    "numberOfThreads must be > 0"
            );
        }
    }

    private static Map<String, Double>
    validateAndCopyDrifts(
            Map<String, Double> drifts) {

        Objects.requireNonNull(
                drifts,
                "drifts must not be null"
        );

        if (drifts.isEmpty()) {
            throw new IllegalArgumentException(
                    "drifts must not be empty"
            );
        }

        Map<String, Double> copy =
                new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry
                : drifts.entrySet()) {

            String symbol =
                    validateSymbol(entry.getKey());

            Double drift =
                    Objects.requireNonNull(
                            entry.getValue(),
                            "drift must not be null"
                    );

            if (!Double.isFinite(drift)) {
                throw new IllegalArgumentException(
                        "drift must be finite for "
                        + symbol
                );
            }

            copy.put(symbol, drift);
        }

        return Map.copyOf(copy);
    }

    private static Map<String, Double>
    validateAndCopyVolatilities(
            Map<String, Double> volatilities) {

        Objects.requireNonNull(
                volatilities,
                "volatilities must not be null"
        );

        if (volatilities.isEmpty()) {
            throw new IllegalArgumentException(
                    "volatilities must not be empty"
            );
        }

        Map<String, Double> copy =
                new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry
                : volatilities.entrySet()) {

            String symbol =
                    validateSymbol(entry.getKey());

            Double volatility =
                    Objects.requireNonNull(
                            entry.getValue(),
                            "volatility must not be null"
                    );

            if (!Double.isFinite(volatility)
                    || volatility < 0.0) {

                throw new IllegalArgumentException(
                        "volatility must be finite and >= 0 for "
                        + symbol
                );
            }

            copy.put(symbol, volatility);
        }

        return Map.copyOf(copy);
    }

    private static String validateSymbol(
            String symbol) {

        Objects.requireNonNull(
                symbol,
                "asset symbol must not be null"
        );

        String normalized =
                symbol.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    "asset symbol must not be blank"
            );
        }

        return normalized;
    }

    @Override
    public String toString() {
        return "SimulationParameters{" +
                "horizon=" + horizon +
                ", numberOfSteps=" + numberOfSteps +
                ", numberOfPaths=" + numberOfPaths +
                ", seed=" + seed +
                ", numberOfThreads=" + numberOfThreads +
                ", drifts=" + drifts +
                ", volatilities=" + volatilities +
                '}';
    }
}
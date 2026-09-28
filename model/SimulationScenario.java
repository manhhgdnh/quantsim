package model;

import java.util.Objects;

/**
 * Represents one simulated future market scenario.
 *
 * <p>A scenario contains the terminal market state produced
 * by the simulation engine for a single Monte Carlo path.</p>
 *
 * <p>The class is immutable.</p>
 */
public final class SimulationScenario {

    private final long scenarioId;
    private final MarketState terminalState;

    /**
     * Creates a simulation scenario.
     *
     * @param scenarioId unique non-negative identifier
     * @param terminalState terminal market state of the scenario
     *
     * @throws IllegalArgumentException if scenarioId is negative
     * @throws NullPointerException if terminalState is null
     */
    public SimulationScenario(
            long scenarioId,
            MarketState terminalState) {

        if (scenarioId < 0) {
            throw new IllegalArgumentException(
                    "scenarioId must be >= 0"
            );
        }

        this.scenarioId = scenarioId;

        this.terminalState = Objects.requireNonNull(
                terminalState,
                "terminalState must not be null"
        );
    }

    /**
     * Returns the scenario identifier.
     *
     * @return scenario identifier
     */
    public long getScenarioId() {
        return scenarioId;
    }

    /**
     * Returns the terminal market state.
     *
     * @return terminal market state
     */
    public MarketState getTerminalState() {
        return terminalState;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SimulationScenario other)) {
            return false;
        }

        return scenarioId == other.scenarioId
                && terminalState.equals(other.terminalState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                scenarioId,
                terminalState
        );
    }

    @Override
    public String toString() {
        return "SimulationScenario{" +
                "scenarioId=" + scenarioId +
                ", terminalState=" + terminalState +
                '}';
    }
}
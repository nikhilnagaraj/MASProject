import com.github.rinde.rinsim.geom.Point;

import java.util.UUID;

public class TaxiExplorationAnt extends Ant {
    private final int DEFAULT_PHEROMONE_LIFETIME = 1000; // number of ticks a pheromone dropped by ant will last
    private int numAntGenerations; // number of nodes an ant or its replicas can travel until they die

    private double currentBatteryPercent;
    private Point currentSpotOfAgent; // TODO: use Nodes instead of Point?
    private double range;

    public TaxiExplorationAnt(UUID ID, int numAntGenerations, double currentBatteryPercent, Point currentSpotOfAgent, double range) {
        super(ID);
        this.numAntGenerations = numAntGenerations;
        this.currentBatteryPercent = currentBatteryPercent;
        this.currentSpotOfAgent = currentSpotOfAgent;
        this.range = range;
    }

    public int getPheromoneLifetime(){
        return DEFAULT_PHEROMONE_LIFETIME;
    }

    public int getNumAntGenerations() {
        return numAntGenerations;
    }

    public double getCurrentBatteryPercent() {
        return currentBatteryPercent;
    }

    public Point getCurrentSpotOfAgent(){
        return currentSpotOfAgent;
    }

    /***
     * Check if current candidate is already reserved by other agent
     * @param candidate
     * @return
     */
    private boolean checkCandidateForIntentionPheromones(Candidate candidate){
        return !candidate.getPheromoneInfrastructure().getChargeIntentionPheromoneDetails().isEmpty()
                && !candidate.getPheromoneInfrastructure().getTaxiIntentionPheromoneDetails().isEmpty();
    }

    /***
     * Calculate the strength of the pheromone that ant will drop on current candidate
     * @return double between 0 and 1. High value corresponds to high interest of Taxi to be charged at current Candidate.
     */
    public double calculateStrengthOfPheromone(Candidate candidate) {
        if (checkIfAgentInRange(candidate)) {
            double rangeHeuristic = calcRangeHeuristic(candidate);
            // Using the sigmoid function as a heuristic, the strength of the pheromone on a scale of 0 to 1.
            if (rangeHeuristic != 0.0 && this.currentBatteryPercent != 0.0)
                return sigmoid(rangeHeuristic * this.currentBatteryPercent);
            else {
                if (this.currentBatteryPercent != 0.0)
                    return sigmoid(this.currentBatteryPercent);
                else {
                    if (rangeHeuristic != 0.0)
                        return sigmoid(rangeHeuristic);
                    else
                        return 1.0;
                }
            }
        } else {
            return 0.0;
        }
    }

    /***
     * Calculates a heuristic based on the range of the candidates distance from the taxi agents current position
     * @param candidate
     * @return 1 if
     */
    private double calcRangeHeuristic(Candidate candidate){
        return candidate.getDistanceFrom(this.getCurrentSpotOfAgent()) / range;
    }

    /***
     * Check if the owner can reach the gicen candidate within its battery capacities
     * @param candidate
     * @return true, if candidate in range; false otherwise
     */
    private boolean checkIfAgentInRange(Candidate candidate){
        return (candidate.getDistanceFrom(this.getCurrentSpotOfAgent()) <= range);
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public double getRange() {
        return range;
    }
}

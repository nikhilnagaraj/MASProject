import com.github.rinde.rinsim.geom.Point;

import java.util.UUID;

public class TaxiExplorationAnt extends Ant {
    private final int DEFAULT_PHEROMONE_LIFETIME = 1000; // number of ticks a pheromone dropped by ant will last
    private int numAntGenerations; // number of nodes an ant or its replicas can travel until they die

    private double currentBatteryCapacity;
    private Point currentSpotOfAgent; // TODO: use Nodes instead of Point?
    private double range;

    public TaxiExplorationAnt(UUID ID, int numAntGenerations, double currentBatteryCapacity, Point currentSpotOfAgent) {
        super(ID);
        this.numAntGenerations = numAntGenerations;
        this.currentBatteryCapacity = currentBatteryCapacity;
        this.currentSpotOfAgent = currentSpotOfAgent;
    }

    public int getPheromoneLifetime(){
        return DEFAULT_PHEROMONE_LIFETIME;
    }

    public int getNumAntGenerations() {
        return numAntGenerations;
    }

    public double getCurrentBatteryCapacity(){
        return currentBatteryCapacity;
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
    public double calculateStrengthOfPheromone() {
        // TODO implement heuristic for how strong the pheromone should be based on distance of taxi (or pick-up/delivery-spot) to station
        // TODO and current battery capacity of agent
        return 1.0;
    }

    /***
     * Calculates a heuristic based on the range of the candidates distance from the taxi agents current position
     * @param candidate
     * @return 1 if
     */
    private double calcRangeHeuristic(Candidate candidate){
        // TODO: use real distance instead of euclidean distance (if time left)
        double euclideanDistance =
                Math.sqrt(Math.pow((candidate.getPosition().x - this.currentSpotOfAgent.x), (double) 2)
                        + Math.pow((candidate.getPosition().y - this.currentSpotOfAgent.y), (double) 2));
        if(euclideanDistance <= range){
            return (euclideanDistance / range); // TODO
        }
        return 0.0;
    }

    /***
     * Check if the owner can reach the gicen candidate within its battery capacities
     * @param candidate
     * @return true, if candidate in range; false otherwise
     */
    private boolean checkIfAgentInRange(Candidate candidate){
        // TODO: use real distance instead of euclidean distance (if time left)
        double euclideanDistance =
                Math.sqrt(Math.pow((candidate.getPosition().x - this.currentSpotOfAgent.x), (double) 2)
                        + Math.pow((candidate.getPosition().y - this.currentSpotOfAgent.y), (double) 2));
        return (euclideanDistance <= range);
    }
}

import com.github.rinde.rinsim.geom.Point;

public class TaxiExplorationAnt extends Ant {
    int DEFAULT_LIFETIME = 1000; // TODO: change default values for values based on heuristic

    private double currentBatteryCapacity;
    private Point currentSpotOfAgent; // TODO: use Nodes instead of Point
    private double range;

    public IntentionPlan deployAnt(Candidate candidate) {
        double strength = calculateStrengthOfPheromone();
        candidate.getPheromoneInfrastructure().dropPheromone(super.ownerId, new TaxiExplorationPheromone(DEFAULT_LIFETIME, super.ownerId, strength));
        return null;
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
    private double calculateStrengthOfPheromone() {
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
        // TODO: use real distance instead of euclidean distance
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
        // TODO: use real distance instead of euclidean distance
        double euclideanDistance =
                Math.sqrt(Math.pow((candidate.getPosition().x - this.currentSpotOfAgent.x), (double) 2)
                        + Math.pow((candidate.getPosition().y - this.currentSpotOfAgent.y), (double) 2));
        return (euclideanDistance <= range);
    }
}

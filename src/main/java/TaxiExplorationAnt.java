import com.github.rinde.rinsim.geom.Point;

import java.util.HashSet;
import java.util.Map;

public class TaxiExplorationAnt extends Ant {
    private int DEFAULT_PHEROMONE_LIFETIME = 1000; // number of ticks a pheromone dropped by ant will last
    private int ant_lifeTime; // number of nodes an ant or its replicas can travel until they die

    private double currentBatteryCapacity;
    private Point currentSpotOfAgent; // TODO: use Nodes instead of Point?
    private double range;

    public TaxiExplorationAnt(int ant_lifeTime, double currentBatteryCapacity, Point currentSpotOfAgent){
        this.ant_lifeTime = ant_lifeTime;
        this.currentBatteryCapacity = currentBatteryCapacity;
        this.currentSpotOfAgent = currentSpotOfAgent;
    }

    /***
     * deploys ant at a given candidate and returns best intention plan
     * @param candidate
     * @return best intention plan for candidate
     */
    public IntentionPlan deployAnt(Candidate candidate) {
        visitOtherNodes(candidate);
        double strength = calculateStrengthOfPheromone();
        PheromoneInfrastructure pheromoneInfrastructure = candidate.getPheromoneInfrastructure();
        pheromoneInfrastructure.dropPheromone(super.ownerId, new TaxiExplorationPheromone(DEFAULT_PHEROMONE_LIFETIME, super.ownerId, strength));
        Map<String, TaxiIntentionPheromone> taxiIntentionPheromonesOnDeployedNode = pheromoneInfrastructure.getTaxiIntentionPheromoneDetails();
        if(!taxiIntentionPheromonesOnDeployedNode.isEmpty() && !taxiIntentionPheromonesOnDeployedNode.containsKey(ownerId)){
            // another ant already reserved this node for charging --> no intentions to go there
            return null;
        }
        // TODO exploration ant needs to know about charging situation on other nodes to make decision and return concrete intention plan
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

    /***
     * send replicas to other nodes to check on best intention plan
     * @param candidate
     * @return
     */
    private IntentionPlan visitOtherNodes(Candidate candidate){
        HashSet<IntentionPlan> intentionPlans = new HashSet<IntentionPlan>();
        for(Candidate c : candidate.getOtherCandidates()) {
            TaxiExplorationAnt explorationAnt = new TaxiExplorationAnt(ant_lifeTime - 1, currentBatteryCapacity, currentSpotOfAgent);
            intentionPlans.add(explorationAnt.deployAnt(c));
        }
        return chooseBestIntentionPlan(intentionPlans);
    }

    /***
     * use this method to determine the best intention plan given a hashset of intention plans
     * @param intentionPlans - the intention plans discovered by the ants in this step
     * @return best intention plan that the taxi should adhere to
     */
    private IntentionPlan chooseBestIntentionPlan(HashSet<IntentionPlan> intentionPlans){
        for(IntentionPlan intentionPlan : intentionPlans){
            // TODO
        }
        return null;
    }
}

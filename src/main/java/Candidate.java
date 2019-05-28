import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Depot;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Candidate extends Depot {
    private UUID uniqueID;
    private PheromoneInfrastructure pheromoneInfrastructure;
    private Point position;
    private boolean chargingAgentAvailable = false;
    private Set<Candidate> otherCandidates;


    Candidate(PheromoneInfrastructure pheromoneInfrastructure, Point position) {
        super(position);
        this.pheromoneInfrastructure = pheromoneInfrastructure;
        this.position = position;
        uniqueID = UUID.randomUUID();
    }

    /***
     * deploys ant at a given candidate and returns best intention plan
     * @param ant
     * @return best intention plan for candidate
     */
    public IntentionPlan deployTaxiExplorationAnt(TaxiExplorationAnt ant) {
        visitOtherNodes(ant);
        double strength = ant.calculateStrengthOfPheromone();
        pheromoneInfrastructure.dropPheromone(ant.getOwnerId(), new TaxiExplorationPheromone(ant.getPheromoneLifetime(), ant.getOwnerId(), strength));
        Map<String, TaxiIntentionPheromone> taxiIntentionPheromonesOnDeployedNode = pheromoneInfrastructure.getTaxiIntentionPheromoneDetails();
        if(!taxiIntentionPheromonesOnDeployedNode.isEmpty() && !taxiIntentionPheromonesOnDeployedNode.containsKey(ant.getOwnerId())){
            // another ant already reserved this node for charging --> no intentions to go there
            return null;
        }
        // TODO exploration ant needs to know about charging situation on other nodes to make decision and return concrete intention plan
        return null;
    }

    /***
     * send replicas to other nodes to check on best intention plan
     * @return
     */
    private IntentionPlan visitOtherNodes(TaxiExplorationAnt ant){
        HashSet<IntentionPlan> intentionPlans = new HashSet<IntentionPlan>();
        for(Candidate c : otherCandidates) {
            TaxiExplorationAnt explorationAnt =
                    new TaxiExplorationAnt(ant.getAntLifetime() - 1, ant.getCurrentBatteryCapacity(), ant.getCurrentSpotOfAgent());
            intentionPlans.add(c.deployTaxiExplorationAnt(explorationAnt));
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

    public PheromoneInfrastructure getPheromoneInfrastructure() {
        return pheromoneInfrastructure;
    }

    public Point getPosition() {
        return position;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public Set<Candidate> getOtherCandidates(){
        return this.otherCandidates;
    }

    public void setOtherCandidates(Set<Candidate> otherCandidates) {
        this.otherCandidates = otherCandidates;
    }

    public void chargingAgentArrivesAtLocation() {
        chargingAgentAvailable = true;
    }

    public void chargingAgentLeavesLocation() {
        chargingAgentAvailable = false;
    }
}

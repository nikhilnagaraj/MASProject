import java.util.UUID;

public class TaxiIntentionAnt extends Ant {
    /***
     * travels to a desired node according to the exploration trail left by other exploration ants in order to create a
     * reservation by dropping a TaxiIntentionPheromone
     */

    private long pheromoneLifeTime; // number of ticks the reservation will sustain at the given node
    private UUID targetCandidateId;
    private IntentionPlan intentionPlan;
    // TODO does ant have lifetime?
    // TODO implement logic for local routing to given node (i.e. useful when node is not in immediate range of taxi)

    public TaxiIntentionAnt(UUID ownerID, UUID targetCandidateId, long pheromoneLifeTime, IntentionPlan intentionPlan) {
        super(ownerID);
        this.targetCandidateId = targetCandidateId;
        this.pheromoneLifeTime = pheromoneLifeTime;
        this.intentionPlan = intentionPlan;
    }

    public long getPheromoneLifetime(){
        return pheromoneLifeTime;
    }

    public UUID getTargetCandidateId() {
        return targetCandidateId;
    }

    public IntentionPlan getIntentionPlan() {
        return intentionPlan;
    }
}

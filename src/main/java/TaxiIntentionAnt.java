import java.util.UUID;

/***
 * travels to a desired node according to the exploration trail left by other exploration ants in order to create a
 * reservation by dropping a TaxiIntentionPheromone
 */
public class TaxiIntentionAnt extends Ant {

    private long pheromoneLifeTime; // number of ticks the reservation will sustain at the given node
    private UUID targetCandidateId;
    private IntentionPlan intentionPlan;

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

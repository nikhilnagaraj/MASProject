import java.util.UUID;

public class TaxiIntentionAnt extends Ant {
    /***
     * travels to a desired node according to the exploration trail left by other exploration ants in order to create a
     * reservation by dropping a TaxiIntentionPheromone
     */

    private long pheromoneLifeTime; // number of ticks the reservation will sustain at the given node
    private UUID targetCandidateId;
    // TODO does ant have lifetime?
    // TODO implement logic for local routing to given node (i.e. useful when node is not in immediate range of taxi)

    public TaxiIntentionAnt(UUID ownerID, UUID targetCandidateId, int pheromoneLifeTime) {
        super(ownerID);
        this.targetCandidateId = targetCandidateId;
        this.pheromoneLifeTime = pheromoneLifeTime;
        System.out.println("create TaxiIntentionAnt");
    }

    public long getPheromoneLifetime(){
        return pheromoneLifeTime;
    }

    public UUID getTargetCandidateId() {
        return targetCandidateId;
    }
}

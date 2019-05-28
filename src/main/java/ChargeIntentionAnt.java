import java.util.UUID;

public class ChargeIntentionAnt extends Ant {

    private long pheromoneLifeTime; // number of ticks the reservation will sustain at the given node
    private UUID targetCandidateId;

    public ChargeIntentionAnt(UUID ownerId, UUID targetCandidateId, long pheromoneLifeTime) {
        super(ownerId);
        this.targetCandidateId = targetCandidateId;
        this.pheromoneLifeTime = pheromoneLifeTime;
    }

    public long getPheromoneLifetime() {
        return pheromoneLifeTime;
    }

    public UUID getTargetCandidateId() {
        return targetCandidateId;
    }
}

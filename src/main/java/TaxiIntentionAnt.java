import java.util.ArrayList;

public class TaxiIntentionAnt extends Ant {
    /***
     * travels to a desired node according to the exploration trail left by other exploration ants in order to create a
     * reservation by dropping a TaxiIntentionPheromone
     */

    private long pheromoneLifeTime; // number of ticks the reservation will sustain at the given node
    private String targetCandidateId;
    // TODO does ant have lifetime?
    // TODO implement logic for local routing to given node (i.e. useful when node is not in immediate range of taxi)

    public TaxiIntentionAnt(String targetCandidateId, int pheromoneLifeTime){
        this.targetCandidateId = targetCandidateId;
        this.pheromoneLifeTime = pheromoneLifeTime;
    }

    public long getPheromoneLifetime(){
        return pheromoneLifeTime;
    }

    public String getTargetCandidateId(){
        return targetCandidateId;
    }
}

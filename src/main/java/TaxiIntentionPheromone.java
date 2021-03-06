import java.util.UUID;

public class TaxiIntentionPheromone extends Pheromone {
    /**
     * TaxiIntentionAnts drop TaxiIntentionPheromones in order to reserve a Candidate
     * @param lifeTime number of ticks the reservation is valid
     * @param ownerId originator of the Ant that dropped the intention pheromone
     */

    public TaxiIntentionPheromone(long lifeTime, UUID ownerId) {
        super(lifeTime, ownerId);
    }

    @Override
    public void onDecrementLifeTime(double portionLifetimeRemaining) {
        //Do Nothing
    }
}

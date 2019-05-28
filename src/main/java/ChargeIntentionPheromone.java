public class ChargeIntentionPheromone extends Pheromone {
    /***
     * ChargeIntentionAnts drop ChargeIntentionPheromones to signal that they intend to visit a Candidate in the near future
     * @param lifeTime
     * @param ownerId
     */
    public ChargeIntentionPheromone(long lifeTime, String ownerId) {
        super(lifeTime, ownerId);
    }

    @Override
    public void onDecrementLifeTime(double portionLifetimeRemaining) {
        //Do nothing
    }
}

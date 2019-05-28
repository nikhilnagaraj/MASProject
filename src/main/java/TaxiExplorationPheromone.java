public class TaxiExplorationPheromone extends Pheromone {
    private double initStrength; // strength within [0,1] determined by ant based on taxis position and current battery capacity
    private double currStrength; // current strength

    public TaxiExplorationPheromone(long lifeTime, String ownerId, double strength) {
        super(lifeTime, ownerId);
        this.initStrength = strength;
    }

    public double getStrength() {
        return this.currStrength;
    }

    @Override
    public void onDecrementLifeTime(double portionLifetimeRemaining) {
        this.currStrength = this.initStrength * portionLifetimeRemaining;
    }
}

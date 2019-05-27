public class TaxiExplorationPheromone extends Pheromone {
    private double strength; // strength within [0,1] determined by ant based on taxis position and current battery capacity

    public TaxiExplorationPheromone(long lifeTime, String ownerId, double strength) {
        super(lifeTime, ownerId);
        this.strength = strength;
    }
}

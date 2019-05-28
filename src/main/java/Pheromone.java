import java.util.UUID;

public abstract class Pheromone {
    private UUID ownerId; // Id used to uniquely identify owner of pheromone TODO
    private boolean evaporated;
    private long totalLifeTime;
    private long currentLifeTime;

    public Pheromone(long lifeTime, UUID ownerId) {
        this.totalLifeTime = lifeTime;
        this.ownerId = ownerId;
        this.evaporated = false;
        this.currentLifeTime = lifeTime;
    }

    /**
     * decrement the lifetime
     * @return true, if pheromone should evaporate based on lifeTime; false otherwise
     */
    public boolean decrementLifeTime() {
        this.currentLifeTime--;
        onDecrementLifeTime(portionLifetimeRemaining());
        if (this.currentLifeTime < 1) {
            evaporated = true;
        }
        return evaporated;
    }

    public long getLifeTime() {
        return currentLifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.totalLifeTime = lifeTime;
        this.currentLifeTime = lifeTime;
    }

    public boolean isEvaporated() {
        return evaporated;
    }

    /**
     * A modifiable method for specific pheromones to execute additional
     * actions when lifetime is decremented.
     */
    public abstract void onDecrementLifeTime(double portionLifetimeRemaining);

    private UUID getOwnerId() {
        return ownerId;
    }

    private double portionLifetimeRemaining() {
        return ((double) this.currentLifeTime) / this.totalLifeTime;
    }
}

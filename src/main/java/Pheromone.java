public abstract class Pheromone {
    private String ownerId; // Id used to uniquely identify owner of pheromone TODO
    private boolean evaporated;
    private long lifeTime;

    public Pheromone(long lifeTime, String ownerId) {
        this.lifeTime = lifeTime;
        this.ownerId = ownerId;
        this.evaporated = false;
    }

    /**
     * decrement the lifetime
     * @return true, if pheromone should evaporate based on lifeTime; false otherwise
     */
    public boolean decrementLifeTime() {
        this.lifeTime--;
        if(this.lifeTime < 1){
            evaporated = true;
        }
        return evaporated;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public boolean isEvaporated() {
        return evaporated;
    }

    private String getOwnerId(){ return ownerId; }
}

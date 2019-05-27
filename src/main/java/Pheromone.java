public abstract class Pheromone {
    private boolean evaporated;
    private long lifeTime;

    public Pheromone(long lifeTime) {
        this.lifeTime = lifeTime;
        this.evaporated = false;
    }

    /**
     * decrement the lifetime
     * @return true, if pheromone should evaporate based on lifeTime; false otherwise
     */
    public boolean decrementLifeTime() {
        this.lifeTime--;
        if(this.lifeTime < 1){
            this.evaporated = true;
            return true;
        }
        return false;
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
}

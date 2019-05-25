import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;

public class Pheromone implements TickListener {
    private boolean evaporated;
    private double value;
    private long lifeTime;
    private double decay;

    public Pheromone(long lifeTime, double decay) {
        this.lifeTime = lifeTime;
        this.evaporated = false;
        this.decay = decay;
        this.value = 1.0;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    public void setDecay(double decay) {
        this.decay = decay;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public double getDecay() {
        return this.decay;
    }

    public double getValue(){
        return this.value;
    }

    public boolean isEvaporated() {
        return this.evaporated;
    }

    /**
     * decrease value and lifetime of the pheromone with every timestep
     * @param timeLapse
     */
    @Override
    public void tick(TimeLapse timeLapse) {
        if(this.lifeTime > 0) {
            this.value *= this.decay;
            this.lifeTime--;
        } else {
            this.value = 0;
            this.evaporated = true;
        }
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {

    }
}

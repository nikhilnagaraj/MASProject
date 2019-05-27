import core.model.time.TickListener;
import core.model.time.TimeLapse;
import java.util.ArrayList;

public class PheromoneInfrastructure implements TickListener {
    private ArrayList<Pheromone> pheromones;

    public ArrayList<Pheromone> smell(){
        return this.pheromones;
    }

    public void drop(Pheromone pheromone){
        this.pheromones.add(pheromone);
    }

    public boolean evaporate(Pheromone pheromone){
        return this.pheromones.remove(pheromone);
    }

    @Override
    public void tick(TimeLapse timeLapse) {
        for(Pheromone pheromone : this.pheromones){
            boolean shouldEvaporate = pheromone.decrementLifeTime();
            if(shouldEvaporate){
                evaporate(pheromone);
            }
        }
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {

    }
}

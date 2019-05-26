import java.util.ArrayList;

public abstract class Candidate {
    ArrayList<Pheromone> pheromones;

    public Candidate(ArrayList<Pheromone> pheromones) {
    }

    public void dropPheromone(Pheromone pheromone){
        this.pheromones.add(pheromone);
    }

    public ArrayList<Pheromone> getPheromones() {
        return pheromones;
    }
}

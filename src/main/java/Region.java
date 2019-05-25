import java.util.ArrayList;

public class Region {
    private ArrayList<Pheromone> pheromones;
    private Region leftNeighbouringRegion;
    private Region topNeighbouringRegion;
    private Region rightNeighbouringRegion;
    private Region bottomNeighbouringRegion;

    public Region() {
    }

    public void dropPheromone(Pheromone pheromone){
        this.pheromones.add(pheromone);
    }
}

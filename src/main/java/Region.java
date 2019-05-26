import java.util.ArrayList;

public class Region extends Candidate{
    private ArrayList<Pheromone> pheromones;
    private Region leftNeighbouringRegion;
    private Region topNeighbouringRegion;
    private Region rightNeighbouringRegion;
    private Region bottomNeighbouringRegion;

    public Region(ArrayList<Pheromone> pheromones) {
        super(pheromones);
    }
}

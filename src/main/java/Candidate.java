import com.github.rinde.rinsim.geom.Point;
import java.util.ArrayList;

public class Candidate{
    private PheromoneInfrastructure pheromoneInfrastructure;
    private Point position;
    private ArrayList<Candidate> otherCandidates; // TODO: has notion of other candidates yet

    public Candidate(PheromoneInfrastructure pheromoneInfrastructure, Point position) {
        this.pheromoneInfrastructure = pheromoneInfrastructure;
        this.position = position;
    }

    public PheromoneInfrastructure getPheromoneInfrastructure() {
        return pheromoneInfrastructure;
    }

    public Point getPosition() { return position; }
}

import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Depot;

import java.util.Set;
import java.util.UUID;

public class Candidate extends Depot {
    private UUID uniqueID; //TODO: A unique id to denote each candidate.
    private PheromoneInfrastructure pheromoneInfrastructure;
    private Point position;
    private Set<Candidate> otherCandidates;

    Candidate(PheromoneInfrastructure pheromoneInfrastructure, Point position) {
        super(position);
        this.pheromoneInfrastructure = pheromoneInfrastructure;
        this.position = position;

    }

    public PheromoneInfrastructure getPheromoneInfrastructure() {
        return pheromoneInfrastructure;
    }

    public Point getPosition() {
        return position;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public void setOtherCandidates(Set<Candidate> otherCandidates) {
        this.otherCandidates = otherCandidates;
    }
}

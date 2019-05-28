import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Depot;

import java.util.Set;
import java.util.UUID;

public class Candidate extends Depot {
    private UUID uniqueID;
    private PheromoneInfrastructure pheromoneInfrastructure;
    private Point position;
    private boolean chargingAgentAvailable = false;
    private Set<Candidate> otherCandidates;


    Candidate(PheromoneInfrastructure pheromoneInfrastructure, Point position) {
        super(position);
        this.pheromoneInfrastructure = pheromoneInfrastructure;
        this.position = position;
        uniqueID = UUID.randomUUID();

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

    public void chargingAgentArrivesAtLocation() {
        chargingAgentAvailable = true;
    }

    public void chargingAgentLeavesLocation() {
        chargingAgentAvailable = false;
    }
}

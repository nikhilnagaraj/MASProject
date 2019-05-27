public abstract class Candidate {
    private PheromoneInfrastructure pheromoneInfrastructure;

    public Candidate(PheromoneInfrastructure pheromoneInfrastructure) {
        this.pheromoneInfrastructure = pheromoneInfrastructure;
    }

    public PheromoneInfrastructure getPheromoneInfrastructure() {
        return pheromoneInfrastructure;
    }
}

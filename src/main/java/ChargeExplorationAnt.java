import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChargeExplorationAnt extends Ant {

    private Set<CandidateData> taxiPheromoneStrengthData = new HashSet<CandidateData>();

    // To allow for limited replication of charginExploration ants, change implementation to follow TaxiExplorationAnts.
    // private int numAntGenerations; // number of nodes an ant or its replicas can travel until they die

    public ChargeExplorationAnt(UUID ownerId) {
        super(ownerId);
    }

    public void smellTaxiPheromones(Candidate candidate) {
        CandidateData cData = new CandidateData(candidate,
                candidate.getPheromoneInfrastructure().smellTaxiExplorationPheromones());
        taxiPheromoneStrengthData.add(cData);
        visitOtherNodes(candidate);
    }

    private void visitOtherNodes(Candidate candidate) {

        for (Candidate candidate1 : candidate.getOtherCandidates()) {
            CandidateData candidateData = new CandidateData(candidate1,
                    candidate1.getPheromoneInfrastructure().smellTaxiExplorationPheromones());
            taxiPheromoneStrengthData.add(candidateData);
        }

    }

    public Set<CandidateData> getTaxiPheromoneStrengthData() {
        return taxiPheromoneStrengthData;
    }

    @Override
    public String toString() {
        String result = "Data obtained by exploration ant is as follows: \n";

        for (CandidateData cData : taxiPheromoneStrengthData) {
            result += cData.toString();
        }

        return result;
    }

    class CandidateData {
        Candidate candidate;
        double taxiPheromoneStrength;
        UUID uniqueID;

        public CandidateData(Candidate candidate, double taxiPheromoneStrength) {
            this.candidate = candidate;
            this.taxiPheromoneStrength = taxiPheromoneStrength;
            this.uniqueID = candidate.getUniqueID();
        }

        public Candidate getCandidate() {
            return candidate;
        }

        public double getTaxiPheromoneStrength() {
            return taxiPheromoneStrength;
        }

        public UUID getUniqueID() {
            return uniqueID;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if (!CandidateData.class.isAssignableFrom(obj.getClass()))
                return false;

            CandidateData cData = (CandidateData) obj;

            return cData.getUniqueID().equals(this.uniqueID);
        }

        @Override
        public String toString() {
            return String.format("Pheromone Strength at " +
                    uniqueID.toString() + " - " + String.valueOf(getTaxiPheromoneStrength()) + ".\n");
        }

    }
}

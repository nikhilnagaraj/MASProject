import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChargeExplorationAnt extends Ant {

    Set<CandidateData> taxiPheromoneStrengthData = new HashSet<CandidateData>();

    public void smellTaxiPheromones(Candidate candidate) {
        CandidateData cData = new CandidateData(candidate, candidate.getPheromoneInfrastructure().smellTaxiExplorationPheromones());
        taxiPheromoneStrengthData.add(cData);
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

    }
}

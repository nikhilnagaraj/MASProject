import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExplorationReport {

    private final UUID ownerID;
    private Map<Candidate, TaxiCandidateData> candidateInfo;

    ExplorationReport(UUID ownerID) {
        this.ownerID = ownerID;
        candidateInfo = new HashMap<Candidate, TaxiCandidateData>();
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    public Map<Candidate, TaxiCandidateData> getCandidateInfo() {
        return candidateInfo;
    }

    public void putReportEntry(Candidate candidate, TaxiCandidateData taxiCandidateData) {
        candidateInfo.put(candidate, taxiCandidateData);
    }

    public void mergeReports(ExplorationReport report) {
        if (report.getOwnerID().equals(this.getOwnerID())) {
            candidateInfo.putAll(report.getCandidateInfo());
        }
    }
}

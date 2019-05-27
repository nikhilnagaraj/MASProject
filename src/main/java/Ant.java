import java.util.ArrayList;

public abstract class Ant{
    private ArrayList<Candidate> candidatePath;
    String ownerId;
    int maxComunicationDepth; // TODO: denotes the number of nodes an ant or its replicas will visit at its maximum

    public Ant() {
    }

    public Ant(String ownerId, ArrayList<Candidate> candidatePath) {
        this.ownerId = ownerId;
        this.candidatePath = new ArrayList<Candidate>(candidatePath);
    }

    // TODO: implement smart wy to replicate and decide where to send replicas
    // TODO: report back to Agent
    public void moveToNextCandidate() throws Exception {
        if(!this.candidatePath.isEmpty()){
            Candidate nextCandidate = candidatePath.get(0);
            // nextCandidate.addAnt(this);
            candidatePath.remove(nextCandidate);
        } else {
            throw new Exception("ant's candidate path is empty - TODO");
        }
    }

    // TODO
    // public abstract IntentionPlan deployAnt();

    // TODO
    // public abstract boolean deployAnt(IntentionPlan plan);

}

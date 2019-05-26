import java.util.ArrayList;

public abstract class Ant{
    private ArrayList<Candidate> candidatePath;

    public Ant(ArrayList<Candidate> candidatePath) {
        this.candidatePath = new ArrayList<Candidate>(candidatePath);
    }

    // TODO
    public void moveToNextCandidate() throws Exception {
        if(!this.candidatePath.isEmpty()){
            Candidate nextCandidate = candidatePath.get(0);
            // nextCandidate.addAnt(this);
            candidatePath.remove(nextCandidate);
        } else {
            throw new Exception("ant's candidate path is empty - TODO");
        }
    }

    public abstract IntentionPlan deployAnt();

    public abstract boolean deployAnt(IntentionPlan plan);

}

import java.util.ArrayList;

public abstract class Ant{
    private ArrayList<Candidate> candidatePath;
    String ownerId;

    public Ant() {
    }

    public Ant(String ownerId, ArrayList<Candidate> candidatePath) {
        this.ownerId = ownerId;
        this.candidatePath = new ArrayList<Candidate>(candidatePath);
    }

    public String getOwnerId(){
        return ownerId;
    }

    // TODO
    // public abstract IntentionPlan deployAnt();

    // TODO
    // public abstract boolean deployAnt(IntentionPlan plan);

}

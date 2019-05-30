
import com.github.rinde.rinsim.geom.Point;

import java.util.UUID;

/**
 * This denotes a feasible plan, as determined by the exploration ant.
 */
public class IntentionPlan {


    private Candidate chosenCandidate;

    public IntentionPlan(Candidate chosenCandidate) {
        this.chosenCandidate = chosenCandidate;
    }

    public UUID getTargetID() {
        return chosenCandidate.getUniqueID();
    }

    public Point getTargetPosition() {
        return chosenCandidate.getPosition();
    }

    public Candidate getChosenCandidate() {
        return chosenCandidate;
    }
}

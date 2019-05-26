
package core.model.pdp;

final class AutoValue_DefaultPDPModel_Builder extends DefaultPDPModel.Builder {

    private static final long serialVersionUID = 165944940216903075L;
    private final TimeWindowPolicy policy;

    AutoValue_DefaultPDPModel_Builder(
            TimeWindowPolicy policy) {
        if (policy == null) {
            throw new NullPointerException("Null policy");
        }
        this.policy = policy;
    }

    @Override
    public TimeWindowPolicy getPolicy() {
        return policy;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DefaultPDPModel.Builder) {
            DefaultPDPModel.Builder that = (DefaultPDPModel.Builder) o;
            return (this.policy.equals(that.getPolicy()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.policy.hashCode();
        return h;
    }

}

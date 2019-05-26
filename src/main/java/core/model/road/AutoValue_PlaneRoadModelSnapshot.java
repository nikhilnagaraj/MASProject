
package core.model.road;

final class AutoValue_PlaneRoadModelSnapshot extends PlaneRoadModelSnapshot {

    private final PlaneRoadModel model;

    AutoValue_PlaneRoadModelSnapshot(
            PlaneRoadModel model) {
        if (model == null) {
            throw new NullPointerException("Null model");
        }
        this.model = model;
    }

    @Override
    public PlaneRoadModel getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "PlaneRoadModelSnapshot{"
                + "model=" + model
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PlaneRoadModelSnapshot) {
            PlaneRoadModelSnapshot that = (PlaneRoadModelSnapshot) o;
            return (this.model.equals(that.getModel()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.model.hashCode();
        return h;
    }

}

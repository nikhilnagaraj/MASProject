
package core.model.road;

import com.github.rinde.rinsim.geom.Graph;
import com.google.common.base.Supplier;

import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;


final class AutoValue_RoadModelBuilders_CachedGraphRMB extends RoadModelBuilders.CachedGraphRMB {

    private static final long serialVersionUID = -7837221650923727573L;
    private final Unit<Length> distanceUnit;
    private final Unit<Velocity> speedUnit;
    private final Supplier<Graph<?>> graphSupplier;

    AutoValue_RoadModelBuilders_CachedGraphRMB(
            Unit<Length> distanceUnit,
            Unit<Velocity> speedUnit,
            Supplier<Graph<?>> graphSupplier) {
        if (distanceUnit == null) {
            throw new NullPointerException("Null distanceUnit");
        }
        this.distanceUnit = distanceUnit;
        if (speedUnit == null) {
            throw new NullPointerException("Null speedUnit");
        }
        this.speedUnit = speedUnit;
        if (graphSupplier == null) {
            throw new NullPointerException("Null graphSupplier");
        }
        this.graphSupplier = graphSupplier;
    }

    @Override
    public Unit<Length> getDistanceUnit() {
        return distanceUnit;
    }

    @Override
    public Unit<Velocity> getSpeedUnit() {
        return speedUnit;
    }

    @Override
    protected Supplier<Graph<?>> getGraphSupplier() {
        return graphSupplier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RoadModelBuilders.CachedGraphRMB) {
            RoadModelBuilders.CachedGraphRMB that = (RoadModelBuilders.CachedGraphRMB) o;
            return (this.distanceUnit.equals(that.getDistanceUnit()))
                    && (this.speedUnit.equals(that.getSpeedUnit()))
                    && (this.graphSupplier.equals(that.getGraphSupplier()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.distanceUnit.hashCode();
        h *= 1000003;
        h ^= this.speedUnit.hashCode();
        h *= 1000003;
        h ^= this.graphSupplier.hashCode();
        return h;
    }

}


package core.model.road;

import com.github.rinde.rinsim.geom.ListenableGraph;
import com.google.common.base.Supplier;

import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

final class AutoValue_RoadModelBuilders_DynamicGraphRMB extends RoadModelBuilders.DynamicGraphRMB {

    private static final long serialVersionUID = 7269626100558413212L;
    private final Unit<Length> distanceUnit;
    private final Unit<Velocity> speedUnit;
    private final Supplier<ListenableGraph<?>> graphSupplier;
    private final boolean modCheckEnabled;

    AutoValue_RoadModelBuilders_DynamicGraphRMB(
            Unit<Length> distanceUnit,
            Unit<Velocity> speedUnit,
            Supplier<ListenableGraph<?>> graphSupplier,
            boolean modCheckEnabled) {
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
        this.modCheckEnabled = modCheckEnabled;
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
    protected Supplier<ListenableGraph<?>> getGraphSupplier() {
        return graphSupplier;
    }

    @Override
    public boolean isModCheckEnabled() {
        return modCheckEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RoadModelBuilders.DynamicGraphRMB) {
            RoadModelBuilders.DynamicGraphRMB that = (RoadModelBuilders.DynamicGraphRMB) o;
            return (this.distanceUnit.equals(that.getDistanceUnit()))
                    && (this.speedUnit.equals(that.getSpeedUnit()))
                    && (this.graphSupplier.equals(that.getGraphSupplier()))
                    && (this.modCheckEnabled == that.isModCheckEnabled());
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
        h *= 1000003;
        h ^= this.modCheckEnabled ? 1231 : 1237;
        return h;
    }

}

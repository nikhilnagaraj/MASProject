
package core.model.road;

import com.github.rinde.rinsim.geom.ListenableGraph;
import com.google.common.base.Supplier;

import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;


final class AutoValue_RoadModelBuilders_CollisionGraphRMB extends RoadModelBuilders.CollisionGraphRMB {

    private static final long serialVersionUID = -5076770082090735004L;
    private final Unit<Length> distanceUnit;
    private final Unit<Velocity> speedUnit;
    private final Supplier<ListenableGraph<?>> graphSupplier;
    private final boolean modCheckEnabled;
    private final double vehicleLength;
    private final double minDistance;

    AutoValue_RoadModelBuilders_CollisionGraphRMB(
            Unit<Length> distanceUnit,
            Unit<Velocity> speedUnit,
            Supplier<ListenableGraph<?>> graphSupplier,
            boolean modCheckEnabled,
            double vehicleLength,
            double minDistance) {
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
        this.vehicleLength = vehicleLength;
        this.minDistance = minDistance;
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
    double getVehicleLength() {
        return vehicleLength;
    }

    @Override
    double getMinDistance() {
        return minDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RoadModelBuilders.CollisionGraphRMB) {
            RoadModelBuilders.CollisionGraphRMB that = (RoadModelBuilders.CollisionGraphRMB) o;
            return (this.distanceUnit.equals(that.getDistanceUnit()))
                    && (this.speedUnit.equals(that.getSpeedUnit()))
                    && (this.graphSupplier.equals(that.getGraphSupplier()))
                    && (this.modCheckEnabled == that.isModCheckEnabled())
                    && (Double.doubleToLongBits(this.vehicleLength) == Double.doubleToLongBits(that.getVehicleLength()))
                    && (Double.doubleToLongBits(this.minDistance) == Double.doubleToLongBits(that.getMinDistance()));
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
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.vehicleLength) >>> 32) ^ Double.doubleToLongBits(this.vehicleLength);
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.minDistance) >>> 32) ^ Double.doubleToLongBits(this.minDistance);
        return h;
    }

}

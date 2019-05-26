
package core.model.road;

import com.github.rinde.rinsim.geom.Point;

import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;


final class AutoValue_RoadModelBuilders_PlaneRMB extends RoadModelBuilders.PlaneRMB {

    private static final long serialVersionUID = 8160700332762443917L;
    private final Unit<Length> distanceUnit;
    private final Unit<Velocity> speedUnit;
    private final Point min;
    private final Point max;
    private final double maxSpeed;

    AutoValue_RoadModelBuilders_PlaneRMB(
            Unit<Length> distanceUnit,
            Unit<Velocity> speedUnit,
            Point min,
            Point max,
            double maxSpeed) {
        if (distanceUnit == null) {
            throw new NullPointerException("Null distanceUnit");
        }
        this.distanceUnit = distanceUnit;
        if (speedUnit == null) {
            throw new NullPointerException("Null speedUnit");
        }
        this.speedUnit = speedUnit;
        if (min == null) {
            throw new NullPointerException("Null min");
        }
        this.min = min;
        if (max == null) {
            throw new NullPointerException("Null max");
        }
        this.max = max;
        this.maxSpeed = maxSpeed;
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
    Point getMin() {
        return min;
    }

    @Override
    Point getMax() {
        return max;
    }

    @Override
    double getMaxSpeed() {
        return maxSpeed;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RoadModelBuilders.PlaneRMB) {
            RoadModelBuilders.PlaneRMB that = (RoadModelBuilders.PlaneRMB) o;
            return (this.distanceUnit.equals(that.getDistanceUnit()))
                    && (this.speedUnit.equals(that.getSpeedUnit()))
                    && (this.min.equals(that.getMin()))
                    && (this.max.equals(that.getMax()))
                    && (Double.doubleToLongBits(this.maxSpeed) == Double.doubleToLongBits(that.getMaxSpeed()));
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
        h ^= this.min.hashCode();
        h *= 1000003;
        h ^= this.max.hashCode();
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.maxSpeed) >>> 32) ^ Double.doubleToLongBits(this.maxSpeed);
        return h;
    }

}

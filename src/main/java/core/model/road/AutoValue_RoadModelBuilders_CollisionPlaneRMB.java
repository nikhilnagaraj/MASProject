
package core.model.road;

import com.github.rinde.rinsim.geom.Point;

import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;


final class AutoValue_RoadModelBuilders_CollisionPlaneRMB extends RoadModelBuilders.CollisionPlaneRMB {

    private static final long serialVersionUID = -4530691576699175212L;
    private final Unit<Length> distanceUnit;
    private final Unit<Velocity> speedUnit;
    private final Point min;
    private final Point max;
    private final double maxSpeed;
    private final double objectRadius;

    AutoValue_RoadModelBuilders_CollisionPlaneRMB(
            Unit<Length> distanceUnit,
            Unit<Velocity> speedUnit,
            Point min,
            Point max,
            double maxSpeed,
            double objectRadius) {
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
        this.objectRadius = objectRadius;
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
    double getObjectRadius() {
        return objectRadius;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RoadModelBuilders.CollisionPlaneRMB) {
            RoadModelBuilders.CollisionPlaneRMB that = (RoadModelBuilders.CollisionPlaneRMB) o;
            return (this.distanceUnit.equals(that.getDistanceUnit()))
                    && (this.speedUnit.equals(that.getSpeedUnit()))
                    && (this.min.equals(that.getMin()))
                    && (this.max.equals(that.getMax()))
                    && (Double.doubleToLongBits(this.maxSpeed) == Double.doubleToLongBits(that.getMaxSpeed()))
                    && (Double.doubleToLongBits(this.objectRadius) == Double.doubleToLongBits(that.getObjectRadius()));
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
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.objectRadius) >>> 32) ^ Double.doubleToLongBits(this.objectRadius);
        return h;
    }

}

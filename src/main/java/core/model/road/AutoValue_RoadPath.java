
package core.model.road;

import com.github.rinde.rinsim.geom.Point;

import java.util.List;


final class AutoValue_RoadPath extends RoadPath {

    private final List<Point> path;
    private final double value;
    private final double travelTime;

    AutoValue_RoadPath(
            List<Point> path,
            double value,
            double travelTime) {
        if (path == null) {
            throw new NullPointerException("Null path");
        }
        this.path = path;
        this.value = value;
        this.travelTime = travelTime;
    }

    @Override
    public List<Point> getPath() {
        return path;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public double getTravelTime() {
        return travelTime;
    }

    @Override
    public String toString() {
        return "RoadPath{"
                + "path=" + path + ", "
                + "value=" + value + ", "
                + "travelTime=" + travelTime
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RoadPath) {
            RoadPath that = (RoadPath) o;
            return (this.path.equals(that.getPath()))
                    && (Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.getValue()))
                    && (Double.doubleToLongBits(this.travelTime) == Double.doubleToLongBits(that.getTravelTime()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.path.hashCode();
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.value) >>> 32) ^ Double.doubleToLongBits(this.value);
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.travelTime) >>> 32) ^ Double.doubleToLongBits(this.travelTime);
        return h;
    }

}

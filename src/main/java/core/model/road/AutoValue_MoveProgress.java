
package core.model.road;

import com.github.rinde.rinsim.geom.Point;
import com.google.common.collect.ImmutableList;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;


final class AutoValue_MoveProgress extends MoveProgress {

    private final Measure<Double, Length> distance;
    private final Measure<Long, Duration> time;
    private final ImmutableList<Point> travelledNodes;

    AutoValue_MoveProgress(
            Measure<Double, Length> distance,
            Measure<Long, Duration> time,
            ImmutableList<Point> travelledNodes) {
        if (distance == null) {
            throw new NullPointerException("Null distance");
        }
        this.distance = distance;
        if (time == null) {
            throw new NullPointerException("Null time");
        }
        this.time = time;
        if (travelledNodes == null) {
            throw new NullPointerException("Null travelledNodes");
        }
        this.travelledNodes = travelledNodes;
    }

    @Override
    public Measure<Double, Length> distance() {
        return distance;
    }

    @Override
    public Measure<Long, Duration> time() {
        return time;
    }

    @Override
    public ImmutableList<Point> travelledNodes() {
        return travelledNodes;
    }

    @Override
    public String toString() {
        return "MoveProgress{"
                + "distance=" + distance + ", "
                + "time=" + time + ", "
                + "travelledNodes=" + travelledNodes
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MoveProgress) {
            MoveProgress that = (MoveProgress) o;
            return (this.distance.equals(that.distance()))
                    && (this.time.equals(that.time()))
                    && (this.travelledNodes.equals(that.travelledNodes()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.distance.hashCode();
        h *= 1000003;
        h ^= this.time.hashCode();
        h *= 1000003;
        h ^= this.travelledNodes.hashCode();
        return h;
    }

}

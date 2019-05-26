
package core.model.road;

import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Point;

final class AutoValue_GraphSpatialRegistry_ConnLoc extends GraphSpatialRegistry.ConnLoc {

    private final Point position;
    private final Connection<?> connection;
    private final double relativePosition;

    AutoValue_GraphSpatialRegistry_ConnLoc(
            Point position,
            Connection<?> connection,
            double relativePosition) {
        if (position == null) {
            throw new NullPointerException("Null position");
        }
        this.position = position;
        if (connection == null) {
            throw new NullPointerException("Null connection");
        }
        this.connection = connection;
        this.relativePosition = relativePosition;
    }

    @Override
    Point position() {
        return position;
    }

    @Override
    Connection<?> connection() {
        return connection;
    }

    @Override
    double relativePosition() {
        return relativePosition;
    }

    @Override
    public String toString() {
        return "ConnLoc{"
                + "position=" + position + ", "
                + "connection=" + connection + ", "
                + "relativePosition=" + relativePosition
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof GraphSpatialRegistry.ConnLoc) {
            GraphSpatialRegistry.ConnLoc that = (GraphSpatialRegistry.ConnLoc) o;
            return (this.position.equals(that.position()))
                    && (this.connection.equals(that.connection()))
                    && (Double.doubleToLongBits(this.relativePosition) == Double.doubleToLongBits(that.relativePosition()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.position.hashCode();
        h *= 1000003;
        h ^= this.connection.hashCode();
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.relativePosition) >>> 32) ^ Double.doubleToLongBits(this.relativePosition);
        return h;
    }

}

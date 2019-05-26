
package core.model.pdp;

import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;


final class AutoValue_VehicleDTO extends VehicleDTO {

    private final Point startPosition;
    private final double speed;
    private final int capacity;
    private final TimeWindow availabilityTimeWindow;

    AutoValue_VehicleDTO(
            Point startPosition,
            double speed,
            int capacity,
            TimeWindow availabilityTimeWindow) {
        if (startPosition == null) {
            throw new NullPointerException("Null startPosition");
        }
        this.startPosition = startPosition;
        this.speed = speed;
        this.capacity = capacity;
        if (availabilityTimeWindow == null) {
            throw new NullPointerException("Null availabilityTimeWindow");
        }
        this.availabilityTimeWindow = availabilityTimeWindow;
    }

    @Override
    public Point getStartPosition() {
        return startPosition;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public TimeWindow getAvailabilityTimeWindow() {
        return availabilityTimeWindow;
    }

    @Override
    public String toString() {
        return "VehicleDTO{"
                + "startPosition=" + startPosition + ", "
                + "speed=" + speed + ", "
                + "capacity=" + capacity + ", "
                + "availabilityTimeWindow=" + availabilityTimeWindow
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof VehicleDTO) {
            VehicleDTO that = (VehicleDTO) o;
            return (this.startPosition.equals(that.getStartPosition()))
                    && (Double.doubleToLongBits(this.speed) == Double.doubleToLongBits(that.getSpeed()))
                    && (this.capacity == that.getCapacity())
                    && (this.availabilityTimeWindow.equals(that.getAvailabilityTimeWindow()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.startPosition.hashCode();
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.speed) >>> 32) ^ Double.doubleToLongBits(this.speed);
        h *= 1000003;
        h ^= this.capacity;
        h *= 1000003;
        h ^= this.availabilityTimeWindow.hashCode();
        return h;
    }

}

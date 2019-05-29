
package core.model.pdp;

import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

final class AutoValue_ParcelDTO extends ParcelDTO {

    private static final long serialVersionUID = -6128057042614968652L;
    private final Point pickupLocation;
    private final Point deliveryLocation;
    private final TimeWindow pickupTimeWindow;
    private final TimeWindow deliveryTimeWindow;
    private final double neededCapacity;
    private final long orderAnnounceTime;
    private final long pickupDuration;
    private final long deliveryDuration;

    AutoValue_ParcelDTO(
            Point pickupLocation,
            Point deliveryLocation,
            TimeWindow pickupTimeWindow,
            TimeWindow deliveryTimeWindow,
            double neededCapacity,
            long orderAnnounceTime,
            long pickupDuration,
            long deliveryDuration) {
        if (pickupLocation == null) {
            throw new NullPointerException("Null pickupLocation");
        }
        this.pickupLocation = pickupLocation;
        if (deliveryLocation == null) {
            throw new NullPointerException("Null deliveryLocation");
        }
        this.deliveryLocation = deliveryLocation;
        if (pickupTimeWindow == null) {
            throw new NullPointerException("Null pickupTimeWindow");
        }
        this.pickupTimeWindow = pickupTimeWindow;
        if (deliveryTimeWindow == null) {
            throw new NullPointerException("Null deliveryTimeWindow");
        }
        this.deliveryTimeWindow = deliveryTimeWindow;
        this.neededCapacity = neededCapacity;
        this.orderAnnounceTime = orderAnnounceTime;
        this.pickupDuration = pickupDuration;
        this.deliveryDuration = deliveryDuration;
    }

    @Override
    public Point getPickupLocation() {
        return pickupLocation;
    }

    @Override
    public Point getDeliveryLocation() {
        return deliveryLocation;
    }

    @Override
    public TimeWindow getPickupTimeWindow() {
        return pickupTimeWindow;
    }

    @Override
    public TimeWindow getDeliveryTimeWindow() {
        return deliveryTimeWindow;
    }

    @Override
    public double getNeededCapacity() {
        return neededCapacity;
    }

    @Override
    public long getOrderAnnounceTime() {
        return orderAnnounceTime;
    }

    @Override
    public long getPickupDuration() {
        return pickupDuration;
    }

    @Override
    public long getDeliveryDuration() {
        return deliveryDuration;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ParcelDTO) {
            ParcelDTO that = (ParcelDTO) o;
            return (this.pickupLocation.equals(that.getPickupLocation()))
                    && (this.deliveryLocation.equals(that.getDeliveryLocation()))
                    && (this.pickupTimeWindow.equals(that.getPickupTimeWindow()))
                    && (this.deliveryTimeWindow.equals(that.getDeliveryTimeWindow()))
                    && (Double.doubleToLongBits(this.neededCapacity) == Double.doubleToLongBits(that.getNeededCapacity()))
                    && (this.orderAnnounceTime == that.getOrderAnnounceTime())
                    && (this.pickupDuration == that.getPickupDuration())
                    && (this.deliveryDuration == that.getDeliveryDuration());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.pickupLocation.hashCode();
        h *= 1000003;
        h ^= this.deliveryLocation.hashCode();
        h *= 1000003;
        h ^= this.pickupTimeWindow.hashCode();
        h *= 1000003;
        h ^= this.deliveryTimeWindow.hashCode();
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.neededCapacity) >>> 32) ^ Double.doubleToLongBits(this.neededCapacity);
        h *= 1000003;
        h ^= (this.orderAnnounceTime >>> 32) ^ this.orderAnnounceTime;
        h *= 1000003;
        h ^= (this.pickupDuration >>> 32) ^ this.pickupDuration;
        h *= 1000003;
        h ^= (this.deliveryDuration >>> 32) ^ this.deliveryDuration;
        return h;
    }

}

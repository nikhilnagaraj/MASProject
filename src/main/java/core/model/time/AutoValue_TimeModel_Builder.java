
package core.model.time;


import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;


final class AutoValue_TimeModel_Builder extends TimeModel.Builder {

    private static final long serialVersionUID = 4197062023514532225L;
    private final long tickLength;
    private final Unit<Duration> timeUnit;

    AutoValue_TimeModel_Builder(
            long tickLength,
            Unit<Duration> timeUnit) {
        this.tickLength = tickLength;
        if (timeUnit == null) {
            throw new NullPointerException("Null timeUnit");
        }
        this.timeUnit = timeUnit;
    }

    @Override
    public long getTickLength() {
        return tickLength;
    }

    @Override
    public Unit<Duration> getTimeUnit() {
        return timeUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TimeModel.Builder) {
            TimeModel.Builder that = (TimeModel.Builder) o;
            return (this.tickLength == that.getTickLength())
                    && (this.timeUnit.equals(that.getTimeUnit()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (this.tickLength >>> 32) ^ this.tickLength;
        h *= 1000003;
        h ^= this.timeUnit.hashCode();
        return h;
    }

}

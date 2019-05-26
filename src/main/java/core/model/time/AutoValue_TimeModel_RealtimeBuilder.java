
package core.model.time;


import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;


final class AutoValue_TimeModel_RealtimeBuilder extends TimeModel.RealtimeBuilder {

    private static final long serialVersionUID = 7255633280244047198L;
    private final long tickLength;
    private final Unit<Duration> timeUnit;
    private final RealtimeClockController.ClockMode clockMode;

    AutoValue_TimeModel_RealtimeBuilder(
            long tickLength,
            Unit<Duration> timeUnit,
            RealtimeClockController.ClockMode clockMode) {
        this.tickLength = tickLength;
        if (timeUnit == null) {
            throw new NullPointerException("Null timeUnit");
        }
        this.timeUnit = timeUnit;
        if (clockMode == null) {
            throw new NullPointerException("Null clockMode");
        }
        this.clockMode = clockMode;
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
    public RealtimeClockController.ClockMode getClockMode() {
        return clockMode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TimeModel.RealtimeBuilder) {
            TimeModel.RealtimeBuilder that = (TimeModel.RealtimeBuilder) o;
            return (this.tickLength == that.getTickLength())
                    && (this.timeUnit.equals(that.getTimeUnit()))
                    && (this.clockMode.equals(that.getClockMode()));
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
        h *= 1000003;
        h ^= this.clockMode.hashCode();
        return h;
    }

}

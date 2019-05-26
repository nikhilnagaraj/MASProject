
package core.model.time;

import com.google.common.collect.Range;


final class AutoValue_RealtimeClockLogger_LogEntry extends RealtimeClockLogger.LogEntry {

    private static final long serialVersionUID = 3044293371048196171L;
    private final Range<Long> tick;
    private final RealtimeClockController.ClockMode clockMode;
    private final Enum<?> clockEvent;

    AutoValue_RealtimeClockLogger_LogEntry(
            Range<Long> tick,
            RealtimeClockController.ClockMode clockMode,
            Enum<?> clockEvent) {
        if (tick == null) {
            throw new NullPointerException("Null tick");
        }
        this.tick = tick;
        if (clockMode == null) {
            throw new NullPointerException("Null clockMode");
        }
        this.clockMode = clockMode;
        if (clockEvent == null) {
            throw new NullPointerException("Null clockEvent");
        }
        this.clockEvent = clockEvent;
    }

    @Override
    public Range<Long> getTick() {
        return tick;
    }

    @Override
    public RealtimeClockController.ClockMode getClockMode() {
        return clockMode;
    }

    @Override
    public Enum<?> getClockEvent() {
        return clockEvent;
    }

    @Override
    public String toString() {
        return "LogEntry{"
                + "tick=" + tick + ", "
                + "clockMode=" + clockMode + ", "
                + "clockEvent=" + clockEvent
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RealtimeClockLogger.LogEntry) {
            RealtimeClockLogger.LogEntry that = (RealtimeClockLogger.LogEntry) o;
            return (this.tick.equals(that.getTick()))
                    && (this.clockMode.equals(that.getClockMode()))
                    && (this.clockEvent.equals(that.getClockEvent()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.tick.hashCode();
        h *= 1000003;
        h ^= this.clockMode.hashCode();
        h *= 1000003;
        h ^= this.clockEvent.hashCode();
        return h;
    }

}

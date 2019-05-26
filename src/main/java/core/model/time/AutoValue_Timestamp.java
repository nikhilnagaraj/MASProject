
package core.model.time;


final class AutoValue_Timestamp extends Timestamp {

    private static final long serialVersionUID = 4952394924947361518L;
    private final long tickCount;
    private final long millis;
    private final long nanos;

    AutoValue_Timestamp(
            long tickCount,
            long millis,
            long nanos) {
        this.tickCount = tickCount;
        this.millis = millis;
        this.nanos = nanos;
    }

    @Override
    public long getTickCount() {
        return tickCount;
    }

    @Override
    public long getMillis() {
        return millis;
    }

    @Override
    public long getNanos() {
        return nanos;
    }

    @Override
    public String toString() {
        return "Timestamp{"
                + "tickCount=" + tickCount + ", "
                + "millis=" + millis + ", "
                + "nanos=" + nanos
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Timestamp) {
            Timestamp that = (Timestamp) o;
            return (this.tickCount == that.getTickCount())
                    && (this.millis == that.getMillis())
                    && (this.nanos == that.getNanos());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (this.tickCount >>> 32) ^ this.tickCount;
        h *= 1000003;
        h ^= (this.millis >>> 32) ^ this.millis;
        h *= 1000003;
        h ^= (this.nanos >>> 32) ^ this.nanos;
        return h;
    }

}

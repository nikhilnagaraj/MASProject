
package core.model.time;


final class AutoValue_RealtimeTickInfo extends RealtimeTickInfo {

    private static final long serialVersionUID = -5816920529507582235L;
    private final Timestamp startTimestamp;
    private final Timestamp endTimestamp;
    private final long interArrivalTime;

    AutoValue_RealtimeTickInfo(
            Timestamp startTimestamp,
            Timestamp endTimestamp,
            long interArrivalTime) {
        if (startTimestamp == null) {
            throw new NullPointerException("Null startTimestamp");
        }
        this.startTimestamp = startTimestamp;
        if (endTimestamp == null) {
            throw new NullPointerException("Null endTimestamp");
        }
        this.endTimestamp = endTimestamp;
        this.interArrivalTime = interArrivalTime;
    }

    @Override
    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }

    @Override
    public long getInterArrivalTime() {
        return interArrivalTime;
    }

    @Override
    public String toString() {
        return "RealtimeTickInfo{"
                + "startTimestamp=" + startTimestamp + ", "
                + "endTimestamp=" + endTimestamp + ", "
                + "interArrivalTime=" + interArrivalTime
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RealtimeTickInfo) {
            RealtimeTickInfo that = (RealtimeTickInfo) o;
            return (this.startTimestamp.equals(that.getStartTimestamp()))
                    && (this.endTimestamp.equals(that.getEndTimestamp()))
                    && (this.interArrivalTime == that.getInterArrivalTime());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.startTimestamp.hashCode();
        h *= 1000003;
        h ^= this.endTimestamp.hashCode();
        h *= 1000003;
        h ^= (this.interArrivalTime >>> 32) ^ this.interArrivalTime;
        return h;
    }

}


package core.model.comm;

import com.google.common.base.Optional;

final class AutoValue_CommModel_Builder extends CommModel.Builder {

    private static final long serialVersionUID = -6598454973114403967L;
    private final double defaultReliability;
    private final Optional<Double> defaultMaxRange;

    AutoValue_CommModel_Builder(
            double defaultReliability,
            Optional<Double> defaultMaxRange) {
        this.defaultReliability = defaultReliability;
        if (defaultMaxRange == null) {
            throw new NullPointerException("Null defaultMaxRange");
        }
        this.defaultMaxRange = defaultMaxRange;
    }

    @Override
    double defaultReliability() {
        return defaultReliability;
    }

    @Override
    Optional<Double> defaultMaxRange() {
        return defaultMaxRange;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CommModel.Builder) {
            CommModel.Builder that = (CommModel.Builder) o;
            return (Double.doubleToLongBits(this.defaultReliability) == Double.doubleToLongBits(that.defaultReliability()))
                    && (this.defaultMaxRange.equals(that.defaultMaxRange()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.defaultReliability) >>> 32) ^ Double.doubleToLongBits(this.defaultReliability);
        h *= 1000003;
        h ^= this.defaultMaxRange.hashCode();
        return h;
    }

}


package core.model.rand;

import com.github.rinde.rinsim.util.StochasticSupplier;
import org.apache.commons.math3.random.RandomGenerator;


final class AutoValue_RandomModel_Builder extends RandomModel.Builder {

    private static final long serialVersionUID = 7985638617806912711L;
    private final long seed;
    private final StochasticSupplier<RandomGenerator> rngSupplier;

    AutoValue_RandomModel_Builder(
            long seed,
            StochasticSupplier<RandomGenerator> rngSupplier) {
        this.seed = seed;
        if (rngSupplier == null) {
            throw new NullPointerException("Null rngSupplier");
        }
        this.rngSupplier = rngSupplier;
    }

    @Override
    long seed() {
        return seed;
    }

    @Override
    StochasticSupplier<RandomGenerator> rngSupplier() {
        return rngSupplier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RandomModel.Builder) {
            RandomModel.Builder that = (RandomModel.Builder) o;
            return (this.seed == that.seed())
                    && (this.rngSupplier.equals(that.rngSupplier()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (this.seed >>> 32) ^ this.seed;
        h *= 1000003;
        h ^= this.rngSupplier.hashCode();
        return h;
    }

}

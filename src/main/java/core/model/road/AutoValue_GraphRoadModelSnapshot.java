
package core.model.road;

import com.github.rinde.rinsim.geom.ConnectionData;
import com.github.rinde.rinsim.geom.ImmutableGraph;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

final class AutoValue_GraphRoadModelSnapshot extends GraphRoadModelSnapshot {

    private final ImmutableGraph<? extends ConnectionData> graph;
    private final Unit<Length> modelDistanceUnit;

    AutoValue_GraphRoadModelSnapshot(
            ImmutableGraph<? extends ConnectionData> graph,
            Unit<Length> modelDistanceUnit) {
        if (graph == null) {
            throw new NullPointerException("Null graph");
        }
        this.graph = graph;
        if (modelDistanceUnit == null) {
            throw new NullPointerException("Null modelDistanceUnit");
        }
        this.modelDistanceUnit = modelDistanceUnit;
    }

    @Override
    public ImmutableGraph<? extends ConnectionData> getGraph() {
        return graph;
    }

    @Override
    public Unit<Length> getModelDistanceUnit() {
        return modelDistanceUnit;
    }

    @Override
    public String toString() {
        return "GraphRoadModelSnapshot{"
                + "graph=" + graph + ", "
                + "modelDistanceUnit=" + modelDistanceUnit
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof GraphRoadModelSnapshot) {
            GraphRoadModelSnapshot that = (GraphRoadModelSnapshot) o;
            return (this.graph.equals(that.getGraph()))
                    && (this.modelDistanceUnit.equals(that.getModelDistanceUnit()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.graph.hashCode();
        h *= 1000003;
        h ^= this.modelDistanceUnit.hashCode();
        return h;
    }

}

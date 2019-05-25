
package Simulator;

final class AutoValue_Simulator_SimulatorModelBuilder extends Simulator.SimulatorModelBuilder {

    private static final long serialVersionUID = -2639242259656135434L;
    private final Simulator simulator;

    AutoValue_Simulator_SimulatorModelBuilder(
            Simulator simulator) {
        if (simulator == null) {
            throw new NullPointerException("Null simulator");
        }
        this.simulator = simulator;
    }

    @Override
    Simulator getSimulator() {
        return simulator;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Simulator.SimulatorModelBuilder) {
            Simulator.SimulatorModelBuilder that = (Simulator.SimulatorModelBuilder) o;
            return (this.simulator.equals(that.getSimulator()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.simulator.hashCode();
        return h;
    }

}

import core.model.pdp.Vehicle;
import core.model.pdp.VehicleDTO;
import core.model.time.TimeLapse;

public class ChargingAgent extends Vehicle {


    /**
     * Instantiate a new vehicle based on the specified properties.
     *
     * @param vehicleDto The data transfer object that holds all vehicle
     *                   properties.
     */
    protected ChargingAgent(VehicleDTO vehicleDto) {
        super(vehicleDto);
    }

    @Override
    protected void tickImpl(TimeLapse time) {

    }
}

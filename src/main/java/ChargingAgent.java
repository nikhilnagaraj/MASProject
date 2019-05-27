import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Vehicle;
import core.model.pdp.VehicleDTO;
import core.model.time.TimeLapse;

public class ChargingAgent extends Vehicle {
    private static final double SPEED = 500d;


    /**
     * Instantiate a new vehicle based on the specified properties.
     *
     * @param vehicleDto The data transfer object that holds all vehicle
     *                   properties.
     */
    protected ChargingAgent(Point startPosition) {
        super(VehicleDTO.builder()
                .capacity(1)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());
    }

    @Override
    protected void tickImpl(TimeLapse time) {

    }
}

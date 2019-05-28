import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Vehicle;
import core.model.pdp.VehicleDTO;
import core.model.road.RoadModel;
import core.model.time.TimeLapse;

public class ChargingAgent extends Vehicle {
    private static final double SPEED = 500d;
    private boolean reserved = false;
    private boolean canMove = false;

    /**
     * Instantiate a new vehicle based on the specified properties.
     *
     * @param startPosition The position at which it is initially placed.
     *
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

        final RoadModel rm = getRoadModel();

    }
}

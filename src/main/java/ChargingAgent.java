import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Vehicle;
import core.model.pdp.VehicleDTO;
import core.model.road.RoadModel;
import core.model.time.TimeLapse;

import java.util.ArrayList;
import java.util.Map;

public class ChargingAgent extends Vehicle {
    private static final double SPEED = 500d;
    private boolean reserved = false;
    private boolean canMove = false;
    private final int minTicksAtLocation;
    private Candidate currentChargingLocation;
    private int ticksAtLocation = 0;

    /**
     * Instantiate a new vehicle based on the specified properties.
     *
     * @param startPosition The position at which it is initially placed.
     *
     */
    ChargingAgent(Point startPosition, int minTicksAtEachLocation) {
        super(VehicleDTO.builder()
                .capacity(1)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());
        minTicksAtLocation = minTicksAtEachLocation;
    }

    @Override
    protected void tickImpl(TimeLapse time) {

        final RoadModel rm = getRoadModel();
        if (currentChargingLocation == null) {
            setCurrentChargingLocation();
        }
        updateCanMove();


    }

    private void updateCanMove() {
        ticksAtLocation++;
        Map<String, TaxiIntentionPheromone> currentReservations =
                currentChargingLocation.getPheromoneInfrastructure().getTaxiIntentionPheromoneDetails();
        canMove = currentReservations.isEmpty() && ticksAtLocation >= minTicksAtLocation;
    }

    private void setCurrentChargingLocation() {
        ArrayList<Candidate> chargingLocationsAtSpot = new ArrayList<Candidate>(getRoadModel().getObjectsAt(this, Candidate.class));
        if (chargingLocationsAtSpot.size() > 1) {
            throw new IllegalArgumentException("Multiple charging locations at the same spot!");
        } else {
            currentChargingLocation = chargingLocationsAtSpot.get(0);
            currentChargingLocation.chargingAgentArrivesAtLocation();
        }
    }
}

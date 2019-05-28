import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Vehicle;
import core.model.pdp.VehicleDTO;
import core.model.road.RoadModel;
import core.model.time.TimeLapse;

import java.util.ArrayList;

public class ChargingAgent extends Vehicle {
    private static final double SPEED = 500d;
    private final int minTicksAtLocation;


    private boolean reserved = false;
    private boolean canMove = false;
    private int ticksAtLocation = 0;
    private Candidate currentChargingLocation;

    private boolean moving = false;
    private Point destination;

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
        if (!moving) {
            if (currentChargingLocation == null) {
                setCurrentChargingLocation();
            }
            updateReserved();
            updateCanMove();
            deployAnts();
        } else {
            /*
            rm.moveTo(this, destination, time);
            if (rm.getPosition(this).equals(destination)) {
                chargingStationArrives();
            }
            */
        }
    }

    private void chargingStationArrives() {
        setCurrentChargingLocation();
        moving = false;
        ticksAtLocation = -1;
        updateReserved();
        updateCanMove();
    }


    private void updateReserved() {
        reserved = !currentChargingLocation.
                getPheromoneInfrastructure().
                getTaxiIntentionPheromoneDetails().isEmpty();
    }

    private void deployAnts() {

        //TODO : Drop ant on current node.
        //TODO : Retrieve info from ant and process data to decide where to move.
        //TODO : Send intention Ant to said location.
        if (canMove) {
            currentChargingLocation.chargingAgentLeavesLocation();
            currentChargingLocation = null;
            moving = true;
            //TODO : Set destination

        }
    }

    private void updateCanMove() {
        ticksAtLocation++;
        canMove = !reserved && ticksAtLocation >= minTicksAtLocation;
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

import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Vehicle;
import core.model.pdp.VehicleDTO;
import core.model.road.RoadModel;
import core.model.time.TimeLapse;

import java.util.*;

public class ChargingAgent extends Vehicle {
    private static final double SPEED = 500d;
    private static final int DEFAULT_INTENTION_PHEROMONE_LIFETIME = 1000; // number of ticks an intention pheromone will last until it evaporates
    private final UUID ID;
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
    ChargingAgent(Point startPosition, int minTicksAtEachLocation, UUID ID) {
        super(VehicleDTO.builder()
                .capacity(1)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());
        minTicksAtLocation = minTicksAtEachLocation;
        this.ID = ID;
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

        //Drop ant on current node.
        ChargeExplorationAnt chargeExplorationAnt = new ChargeExplorationAnt(this.ID);
        chargeExplorationAnt.smellTaxiPheromones(currentChargingLocation);
        if (canMove) {
            // Retrieve info from ant and process data to decide where to move.
            Candidate bestCandidate = getBestNextLocation(chargeExplorationAnt.getTaxiPheromoneStrengthData());
            //Send intention Ant to said location.
            boolean success = currentChargingLocation.sendChargingIntentionAntToLocation(
                    new ChargeIntentionAnt(this.ID, bestCandidate.getUniqueID(),
                            calculatePheromoneLifetime(bestCandidate.getPosition())), bestCandidate);

            //If successfully reserved, start motion
            if (success) {
                currentChargingLocation.chargingAgentLeavesLocation();
                currentChargingLocation = null;
                moving = true;
                this.destination = bestCandidate.getPosition();
            }


        }
    }

    private long calculatePheromoneLifetime(Point bestCandidatePosition) {
        final RoadModel rm = getRoadModel();
        double distance = rm.getDistanceOfPath(rm.getShortestPathTo(rm.getPosition(this), bestCandidatePosition)).getValue();

        return (long) (distance / SPEED) * 3;
    }

    private Candidate getBestNextLocation(Set<ChargeExplorationAnt.CandidateData> taxiPheromoneStrengthData) {
        return Collections.max(taxiPheromoneStrengthData,
                Comparator.comparing((ChargeExplorationAnt.CandidateData cData) -> cData.getTaxiPheromoneStrength()))
                .getCandidate();
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

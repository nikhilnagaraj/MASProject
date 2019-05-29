import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Point;
import com.google.common.base.Optional;
import core.model.pdp.Vehicle;
import core.model.pdp.VehicleDTO;
import core.model.road.GraphRoadModel;
import core.model.road.RoadModel;
import core.model.time.TimeLapse;

import javax.measure.Measure;
import javax.measure.quantity.Length;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ChargingAgent extends Vehicle implements ChargingAgentTaxiInterface {
    private static final double SPEED = 500d;
    private static final int DEFAULT_INTENTION_PHEROMONE_LIFETIME = 1000; // number of ticks an intention pheromone will last until it evaporates
    private final UUID ID;
    private final int minTicksAtLocation;
    private final Logger logger;


    private boolean reserved = false;
    private boolean canMove = false;
    private boolean activeUsage = false;
    private int ticksAtLocation = 0;
    private Candidate currentChargingLocation;

    private boolean moving = false;
    private Point destination;
    private FileHandler fh;

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
        logger = Logger.getLogger(this.ID.toString());
        try {
            setupLogging();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupLogging() throws IOException {
        fh = new FileHandler(String.format("C:\\Users\\nikhi\\OneDrive\\Documents\\clogs\\" + this.ID.toString() + ".log"));
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.setUseParentHandlers(false);
    }

    @Override
    public boolean isActiveUsage() {
        return activeUsage;
    }

    @Override
    public void setActiveUsage(boolean activeUsage) {
        this.activeUsage = activeUsage;
    }

    @Override
    public void taxiLeavesChargingAgent() {
        setActiveUsage(false);
    }

    @Override
    protected void tickImpl(TimeLapse time) {

        logger.info(String.format("The status of the charging agent currently is as follows: \n" +
                "ticksAtLocation: " + String.valueOf(ticksAtLocation) + "\n" +
                "reserved: " + String.valueOf(reserved) + "\n" +
                "canMove: " + String.valueOf(canMove) + "\n" +
                "activeUsage" + String.valueOf(activeUsage) + "\n" +
                "moving:  " + String.valueOf(moving)));
        final RoadModel rm = getRoadModel();
        if (!moving) {
            if (currentChargingLocation == null) {
                setCurrentChargingLocation();
            }
            updateActiveUsage();
            updateReserved();
            updateCanMove();
            deployAnts();
        } else {
            rm.moveTo(this, destination, time);
            if (rm.getPosition(this).equals(destination)) {
                chargingStationArrives();
            }
        }
    }

    private void updateActiveUsage() {
        if (!isActiveUsage()) {
            Taxi taxi = currentChargingLocation.getTaxiFromWaitingList();
            if (taxi != null)
                taxi.setTaxiMode(Taxi.TaxiMode.CHARGING);
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
        this.reserved = !currentChargingLocation.
                getPheromoneInfrastructure().
                getTaxiIntentionPheromoneDetails().isEmpty() || currentChargingLocation.areTaxisWaiting();
    }

    private void deployAnts() {

        //Drop ant on current node.
        ChargeExplorationAnt chargeExplorationAnt = new ChargeExplorationAnt(this.ID);
        chargeExplorationAnt.smellTaxiPheromones(currentChargingLocation);
        logger.info(chargeExplorationAnt.toString());
        if (this.canMove) {
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
        double distance = getDistanceOfPath(rm, rm.getPosition(this), bestCandidatePosition);

        return (long) ((distance / SPEED) * 3600 * 1.5);

    }

    private Candidate getBestNextLocation(Set<ChargeExplorationAnt.CandidateData> taxiPheromoneStrengthData) {
        return Collections.max(taxiPheromoneStrengthData,
                Comparator.comparing((ChargeExplorationAnt.CandidateData cData) -> cData.getTaxiPheromoneStrength()))
                .getCandidate();
    }

    private void updateCanMove() {
        ticksAtLocation++;
        this.canMove = !activeUsage && !reserved && ticksAtLocation >= minTicksAtLocation;
    }

    private void setCurrentChargingLocation() {
        ArrayList<Candidate> chargingLocationsAtSpot = new ArrayList<Candidate>(getRoadModel().getObjectsAt(this, Candidate.class));
        if (chargingLocationsAtSpot.size() > 1) {
            throw new IllegalArgumentException("Multiple charging locations at the same spot!");
        } else {
            currentChargingLocation = chargingLocationsAtSpot.get(0);
            currentChargingLocation.chargingAgentArrivesAtLocation(this);
            currentChargingLocation.getPheromoneInfrastructure().removeChargingIntentionPheromone(this.ID);
        }
    }

    private double getDistanceOfPath(RoadModel rm, Point start, Point destination) {
        Optional<? extends Connection<?>> conn = ((GraphRoadModel) rm).getConnection(this);
        Point from;
        double dist = 0;
        if (conn.isPresent()) {
            dist += Point.distance(start, conn.get().to());
            from = conn.get().to();
        } else {
            from = getRoadModel().getPosition(this);
        }

        List<Point> path = getRoadModel().getShortestPathTo(from, destination);
        Measure<Double, Length> distance = rm.getDistanceOfPath(path);
        // total distance is the sum of distance and dist

        return dist + distance.getValue();
    }
}

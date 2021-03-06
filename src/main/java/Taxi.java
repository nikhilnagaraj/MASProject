/*
 * Copyright (C) 2011-2018 Rinde R.S. van Lon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Point;
import com.google.common.base.Optional;
import core.Simulator;
import core.model.pdp.PDPModel;
import core.model.pdp.Parcel;
import core.model.pdp.Vehicle;
import core.model.pdp.VehicleDTO;
import core.model.road.GraphRoadModel;
import core.model.road.MoveProgress;
import core.model.road.RoadModel;
import core.model.road.RoadModels;
import core.model.time.TimeLapse;

import javax.measure.Measure;
import javax.measure.quantity.Length;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Implementation of a very simple taxi agent. It moves to the closest customer,
 * picks it up, then delivers it, repeat.
 *
 * @author Nikhil Nagaraj, Fabian Fingerhut, Rinde van Lon
 */
class Taxi extends Vehicle implements BatteryTaxiInterface {
    private static final int DEFAULT_EXPLORATION_ANT_LIFETIME = 1; // denotes how many nodes ants can travel sent by this taxi agent
    private static final int DEFAULT_INTENTION_PHEROMONE_LIFETIME = 100; // number of ticks an intention pheromone will last until it evaporates
    private static final int LOW_BATTERY = 15;
    private static final double SPEED = 1000d;

    private final UUID ID;
    private Optional<Parcel> curr;
    private AgentBattery battery;
    private TaxiMode taxiMode;

    static double distanceTravelledToDepot = 0;

    Logger logger;
    private Point chargingLocation;
    private Candidate currentChargingLocation;
    FileHandler fh;
    private Point respawnLocation;

    static double distanceTravelledToChargingAgent = 0;
    static int numOfCustomersPickedUp = 0;
    static int numOfChargings = 0;
    static int numOfDeadBatteries = 0;
    static double distanceTravelledToCustomer = 0;
    // statistics
    public boolean pickedUpCustomerThisTurn;
    public long pickedCustomerWaitingTime;

    public Logger getLogger() {
        return logger;
    }

    Taxi(Point startPosition, int capacity, AgentBattery battery, UUID ID) {
        super(VehicleDTO.builder()
                .capacity(capacity)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());
        this.battery = battery;
        this.taxiMode = TaxiMode.IN_SERVICE;
        this.ID = ID;
        curr = Optional.absent();
        this.battery.setParentTaxi(this);

        logger = Logger.getLogger(this.ID.toString());
        logger.setLevel(Level.ALL);
        try {
            setupLogging();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupLogging() throws IOException {
        fh = new FileHandler(String.format("logs/" + TaxiExample.experimentID + "/" + this.ID.toString() + ".log"));
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.setUseParentHandlers(false);
    }

    public UUID getID() {
        return ID;
    }

    public TaxiMode getTaxiMode() {
        return taxiMode;
    }

    /**
     * This denotes the current status of the Taxi. If true, the taxi will not be aailable to pick customers.
     * If false, the taxi will be active.
     *
     * @param taxiMode
     */
    public void setTaxiMode(TaxiMode taxiMode) {
        this.taxiMode = taxiMode;
    }

    public double getExpectedChargingTime() {
        return this.battery.getExpectedChargingTime();
    }

    public double getBatteryCapacity() {
        return this.battery.getPercentBatteryRemaining();
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {
    }

    @Override
    protected void tickImpl(TimeLapse time) {
        final RoadModel rm = getRoadModel();
        final PDPModel pm = getPDPModel();

        pickedUpCustomerThisTurn = false;
        pickedCustomerWaitingTime = 0;

        logger.info(String.format("Battery remaining before tick: " +
                String.valueOf(this.battery.getPercentBatteryRemaining())));
        logger.info(String.format("TaxiMode before tick: " + taxiMode.toString()));
        if (!taxiMode.equals(TaxiMode.CHARGING)) {
            if (!rm.containsObject(this)) {
                rm.addObjectAt(this, this.respawnLocation);
            }
            taxiNotCharging(rm, time, pm);
        } else if (this.taxiMode.equals(TaxiMode.CHARGING)) {
            battery.charge(time);
        }
        logger.info(String.format("Battery remaining after tick: " +
                String.valueOf(this.battery.getPercentBatteryRemaining())));
        logger.info(String.format("TaxiMode after tick: " + taxiMode.toString()));
    }

    private void taxiNotCharging(RoadModel rm, TimeLapse time, PDPModel pm) {
        if (taxiMode.equals(TaxiMode.IN_SERVICE)) {
            taxiInService(rm, time, pm);
        } else if (this.taxiMode == TaxiMode.NO_SERVICE) {
            taxiOutOfService(rm, time, pm);
        } else if (this.taxiMode == TaxiMode.AWAITING_CHARGE)
            taxiAwaitingCharge(rm, time, pm);
    }

    private void taxiAwaitingCharge(RoadModel rm, TimeLapse time, PDPModel pm) {
        if (currentChargingLocation != null) {
            int chargingPheromoneDetails = currentChargingLocation.
                    getPheromoneInfrastructure().getChargeIntentionPheromoneDetails().size();
            if (chargingPheromoneDetails != 1 && !currentChargingLocation.isChargingAgentAvailable()) {
                logger.info("Taxi LEAVES WAITING QUEUE. No charging intention pheromones/charging agent found.");
                this.taxiMode = TaxiMode.NO_SERVICE;
                currentChargingLocation = null;
                chargingLocation = null;
                if (!rm.containsObject(this))
                    rm.addObjectAt(this, this.respawnLocation);
            }
        } else
            throw new IllegalArgumentException("Taxi awaiting charge at illegal location.");
    }

    private void taxiOutOfService(RoadModel rm, TimeLapse time, PDPModel pm) {
        if (chargingLocation != null)
            moveToChargingAgent(time, rm);
        else
            setupChargingAgentLocation(rm, time);
    }

    private void taxiInService(RoadModel rm, TimeLapse time, PDPModel pm) {

        if (!time.hasTimeLeft()) {
            return;
        }
        if (!curr.isPresent()) {
            dealWithNoCargo(rm, time, pm);
        } else {
            dealWithCargo(rm, time, pm);
        }

    }

    private void dealWithNoCargo(RoadModel rm, TimeLapse time, PDPModel pm) {

        if (this.battery.getPercentBatteryRemaining() > LOW_BATTERY) {
            normalOperation(rm, time, pm);
        } else {
            taxiMode = TaxiMode.NO_SERVICE;
            setupChargingAgentLocation(rm, time);
        }
    }

    private void normalOperation(RoadModel rm, TimeLapse time, PDPModel pm) {

        curr = Optional.fromNullable(RoadModels.findClosestUnallotedObject(
                rm.getPosition(this), rm, Parcel.class));

        if (!curr.isPresent() && chargingLocation == null) {
            if (this.battery.getPercentBatteryRemaining() < 50)
                setupChargingAgentLocation(rm, time);
            else
                moveToDepot(time, rm);
        } else if (curr.isPresent()) {
            if (isPickupNotPossible(rm, curr)) {
                taxiMode = TaxiMode.NO_SERVICE;
                setupChargingAgentLocation(rm, time);
            } else {
                dealWithCargo(rm, time, pm);
            }
        } else if (chargingLocation != null) {
            moveToChargingAgent(time, rm);
        }

    }

    private void dealWithCargo(RoadModel rm, TimeLapse time, PDPModel pm) {

        final boolean inCargo = pm.containerContains(this, curr.get());
        // sanity check: if it is not in our cargo AND it is also not on the
        // RoadModel, we cannot go to curr anymore.
        if (!inCargo && !rm.containsObject(curr.get())) {
            curr = Optional.absent();
        } else if (inCargo) {
            // if it is in cargo and there's juice in the battery, go to its destination
            moveToDeliverParcel(time, rm, pm);
        } else {
            // it is still available, go there as fast as possible
            moveToPickUpParcel(time, rm, pm);
        }
    }

    private void setupChargingAgentLocation(RoadModel rm, TimeLapse time) {

        IntentionPlan iPlan = sendExplorationAnts(this.battery.getPercentBatteryRemaining()
                , rm.getPosition(this));
        boolean success = sendIntentionAnt(iPlan);
        if (success) {
            chargingLocation = iPlan.getTargetPosition();
            moveToChargingAgent(time, rm);
        } else {
            moveToDepot(time, rm);
        }
    }

    private void moveToDepot(TimeLapse time, RoadModel rm) {
        Point depotPosition = rm.getPosition(RoadModels.findClosestObject
                (rm.getPosition(this), rm, TaxiExample.TaxiBase.class));
        //System.out.println(String.format("Depot Position: " + depotPosition.toString()));
        if (this.battery.getCurrentBatteryCapacity() > 0) {
            MoveProgress moveDetails = rm.moveTo(this, depotPosition, time);
            distanceTravelledToDepot += moveDetails.distance().getValue();
            this.battery.discharge(moveDetails);

            if (rm.getPosition(this).equals(depotPosition)) {
                this.respawnLocation = rm.getPosition(this);
                rm.removeObject(this);
            }
        } else {
            logger.severe(String.format("BATTERY_DEAD"));
            sendExplorationAnts(this.battery.getPercentBatteryRemaining(), rm.getPosition(this));
            setTaxiMode(TaxiMode.CHARGING);
            setupCharging();
            rm.objectDischarged(this);
        }
    }

    private void moveToChargingAgent(TimeLapse time, RoadModel rm) {

        if (this.battery.getCurrentBatteryCapacity() > 0) {
            MoveProgress moveDetails = rm.moveTo(this, chargingLocation, time);
            distanceTravelledToChargingAgent += moveDetails.distance().getValue();
            this.battery.discharge(moveDetails);

            if (rm.getPosition(this).equals(chargingLocation)) {
                // getIntoChargingStation
                ArrayList<Candidate> candidateSet = new ArrayList<Candidate>(rm.getObjectsAt(this, Candidate.class));
                if (candidateSet.size() > 1)
                    throw new IllegalArgumentException("Multiple charging locations at single node!");

                Candidate candidate = candidateSet.get(0);
                currentChargingLocation = candidate;
                if (candidate.isChargingAgentAvailable()) {
                    if (!candidate.getChargingAgent().isActiveUsage()) {
                        logger.info("Taxi CONNECTED TO CHARGING AGENT");
                        candidate.getChargingAgent().setActiveUsage(true);
                        this.taxiMode = TaxiMode.CHARGING;
                        this.respawnLocation = this.chargingLocation;
                        rm.removeObject(this);
                    } else {
                        logger.info("Taxi JOINS WAITING QUEUE. Charging Agent available.");
                        candidate.taxiJoinsWaitingQueue(this);
                        this.taxiMode = TaxiMode.AWAITING_CHARGE;
                        this.respawnLocation = this.chargingLocation;
                        rm.removeObject(this);
                    }
                } else {
                    if (candidate.getPheromoneInfrastructure().getChargeIntentionPheromoneDetails().size() > 0) {
                        logger.info("Taxi JOINS WAITING QUEUE. Charging Agent should arrive soon.");
                        candidate.taxiJoinsWaitingQueue(this);
                        this.taxiMode = TaxiMode.AWAITING_CHARGE;
                        this.respawnLocation = this.chargingLocation;
                        rm.removeObject(this);
                    } else {
                        logger.info("Taxi LEAVES CHARGING LOCATION. It doesn't know why it is here.");
                        this.taxiMode = TaxiMode.NO_SERVICE;
                        this.chargingLocation = null;
                        this.currentChargingLocation = null;

                    }
                }

            }
        } else {
            logger.severe(String.format("BATTERY_DEAD"));
            sendExplorationAnts(this.battery.getPercentBatteryRemaining(), rm.getPosition(this));
            setTaxiMode(TaxiMode.CHARGING);
            setupCharging();
            rm.objectDischarged(this);
        }

    }

    private void moveToPickUpParcel(TimeLapse time, RoadModel rm, PDPModel pm) {
        if (this.battery.getCurrentBatteryCapacity() > 0) {
            MoveProgress moveDetails = rm.moveTo(this, curr.get(), time);
            distanceTravelledToCustomer += moveDetails.distance().getValue();
            this.battery.discharge(moveDetails);

            if (rm.equalPosition(this, curr.get())) {
                // pickup customer
                pm.pickup(this, curr.get(), time);
                sendExplorationAnts(this.battery.getPercentBatteryRemaining(), rm.getPosition(this));
                numOfCustomersPickedUp++;
                this.pickedUpCustomerThisTurn = true;
                pickedCustomerWaitingTime = time.getTime() - ((TaxiExample.Customer) curr.get()).startTick;
            }
        } else {
            logger.severe(String.format("BATTERY_DEAD"));
            unallotPassenger(rm);
            sendExplorationAnts(this.battery.getPercentBatteryRemaining(), rm.getPosition(this));
            setTaxiMode(TaxiMode.CHARGING);
            setupCharging();
            rm.objectDischarged(this);

        }
    }

    private void unallotPassenger(RoadModel rm) {
        Parcel newParcel = curr.get();
        newParcel.setAlloted(false);
        Point position = curr.get().getPickupLocation();

        rm.removeObject(curr.get());
        rm.unregister(curr.get());

        rm.register(newParcel);
        rm.addObjectAt(newParcel, position);
    }

    private void moveToDeliverParcel(TimeLapse time, RoadModel rm, PDPModel pm) {

        if (this.battery.getCurrentBatteryCapacity() > 0) {
            MoveProgress moveDetails = rm.moveTo(this, curr.get().getDeliveryLocation(), time);
            this.battery.discharge(moveDetails);

            if (rm.getPosition(this).equals(curr.get().getDeliveryLocation())) {
                // deliver when we arrive
                pm.deliver(this, curr.get(), time);
                sendExplorationAnts(this.battery.getPercentBatteryRemaining(), rm.getPosition(this));
            }
        } else {
            logger.severe(String.format("BATTERY_DEAD"));
            removePassenger(time);
            sendExplorationAnts(this.battery.getPercentBatteryRemaining(), rm.getPosition(this));
            setTaxiMode(TaxiMode.CHARGING);
            setupCharging();
            rm.objectDischarged(this);
        }

    }

    private long getIntentionAntLifetime(Point bestCandidatePosition) {
        final RoadModel rm = getRoadModel();
        double distance = getDistanceOfPath(rm, rm.getPosition(this),
                bestCandidatePosition);

        return (long) ((distance / SPEED) * 3600 * 1.5);
    }


    private boolean isPickupNotPossible(RoadModel rm, Optional<Parcel> curr) {
        return getDistanceOfPath(rm, rm.getPosition(this), curr.get().getPickupLocation()) > this.battery.getCurrentBatteryCapacity();

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


    private void removePassenger(TimeLapse time) {
        if (this.curr.isPresent()) {
            getPDPModel().removeParcel(this, curr.get(), time);
        }
    }

    /**
     * This method sets the random respawn location once a taxi has been totally discharged.
     */
    private void setupCharging() {
        final RoadModel rm = getRoadModel();
        this.respawnLocation = rm.getRandomPosition(Simulator.getRandomGenerator());
        numOfDeadBatteries++;
    }

    @Override
    public void batteryCharged() {
        final RoadModel rm = getRoadModel();
        if (currentChargingLocation != null)
            currentChargingLocation.getChargingAgent().taxiLeavesChargingAgent();
        if (!rm.containsObject(this))
            rm.addObjectAt(this, this.respawnLocation);
        this.taxiMode = TaxiMode.IN_SERVICE;

        this.currentChargingLocation = null;
        this.chargingLocation = null;
        numOfChargings++;

    }

    /***
     * use this method to send exploration ants to all nodes the taxi is aware of
     * @param curBatteryPercent
     * @param curPosition
     * @return Intention plan for taxi
     */
    private IntentionPlan sendExplorationAnts(double curBatteryPercent, Point curPosition) {
        // TODO: Implement this process in a tick-based fashion in a later stage (if time allows it)

        Candidate candidate = getClosestCandidate();
        TaxiExplorationAnt explorationAnt = new TaxiExplorationAnt(this.ID, DEFAULT_EXPLORATION_ANT_LIFETIME,
                curBatteryPercent, curPosition, this.battery.getCurrentBatteryCapacity());
        ExplorationReport report = candidate.deployTaxiExplorationAnt(explorationAnt);
        return chooseBestPlan(report);
    }

    private Candidate getClosestCandidate() {
        final RoadModel rm = getRoadModel();
        return RoadModels.findClosestObject(rm.getPosition(this), rm, Candidate.class);
    }

    private boolean sendIntentionAnt(IntentionPlan iPlan) {
        final RoadModel rm = getRoadModel();
        Candidate closestCandidate =
                RoadModels.findClosestObject(rm.getPosition(this), rm, Candidate.class);
        boolean success = closestCandidate.deployTaxiIntentionAnt(new TaxiIntentionAnt(this.ID,
                iPlan.getTargetID(), getIntentionAntLifetime(iPlan.getTargetPosition()), iPlan));

        return success;
    }

    /***
     * use this method to determine the best intention plan given a hashset of intention plans
     * @param explorationReport - the report discovered by the ants in this step
     * @return best intention plan that the taxi should adhere to
     */
    private IntentionPlan chooseBestPlan(ExplorationReport explorationReport) {

        Candidate chosenCandidate = null;
        TaxiCandidateData taxiCandidateData = null;
        for (Map.Entry<Candidate, TaxiCandidateData> entry : explorationReport.getCandidateInfo().entrySet()) {
            if ((entry.getValue().isChargingAgentPresent() || entry.getValue().isChargingAgentIntentionPresent())
                    && entry.getValue().isWaitingSpotsAvailable()) {
                if (chosenCandidate == null || taxiCandidateData == null) {
                    chosenCandidate = entry.getKey();
                    taxiCandidateData = entry.getValue();
                } else {
                    if (entry.getValue().getExpectedWaitingTime() < taxiCandidateData.getExpectedWaitingTime()) {
                        chosenCandidate = entry.getKey();
                        taxiCandidateData = entry.getValue();
                    }
                }
            }
        }

        IntentionPlan intentionPlan = new IntentionPlan(chosenCandidate);
        return intentionPlan;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Taxi.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Taxi taxi = (Taxi) obj;

        return taxi.getID().equals(this.ID);
    }

    enum TaxiMode {
        IN_SERVICE,
        NO_SERVICE,
        AWAITING_CHARGE,
        CHARGING
    }

}

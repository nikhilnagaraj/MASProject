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

import com.github.rinde.rinsim.geom.Point;
import com.google.common.base.Optional;
import core.Simulator;
import core.model.pdp.PDPModel;
import core.model.pdp.Parcel;
import core.model.pdp.Vehicle;
import core.model.pdp.VehicleDTO;
import core.model.road.MoveProgress;
import core.model.road.RoadModel;
import core.model.road.RoadModels;
import core.model.time.TimeLapse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * Implementation of a very simple taxi agent. It moves to the closest customer,
 * picks it up, then delivers it, repeat.
 *
 * @author Rinde van Lon
 */
class Taxi extends Vehicle implements BatteryTaxiInterface {
    private static final int DEFAULT_EXPLORATION_ANT_LIFETIME = 1; // denotes how many nodes ants can travel sent by this taxi agent
    private static final int DEFAULT_INTENTION_PHEROMONE_LIFETIME = 100; // number of ticks an intention pheromone will last until it evaporates
    private static final double SPEED = 1000d;

    private final UUID ID;

    private Optional<Parcel> curr;
    private AgentBattery battery;
    private TaxiMode taxiMode;
    private Point chargingLocation;
    private double distTravelledPerTrip = 0.0;

    // statistics
    int numOfCustomersPickedUp = 0;
    int numOfCustomersDelivered = 0;
    int numOfChargings = 0;
    int numOfDeadBatteries = 0;

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

    @Override
    public void afterTick(TimeLapse timeLapse) {
    }

    @Override
    protected void tickImpl(TimeLapse time) {
        if (!taxiMode.equals(TaxiMode.CHARGING)) {
            final RoadModel rm = getRoadModel();
            final PDPModel pm = getPDPModel();

            if (!time.hasTimeLeft()) {
                return;
            }
            if (!curr.isPresent()) {
                curr = Optional.fromNullable(RoadModels.findClosestObject(
                        rm.getPosition(this), rm, Parcel.class));

                if (isPickupNotPossible(rm, curr)) {
                    IntentionPlan iPlan = sendExplorationAnts(battery.getPercentBatteryRemaining() / 100,
                            rm.getPosition(this));
                    // Ant newIntentionAnt = new TaxiIntentionAnt();
                    // TODO: targetCandidateId = iPlan.getTargetNode();
                    UUID targetCandidateId = null;
                    sendIntentionAnt(targetCandidateId);
                    /*
                    IntentionPlan iPlan = newExplorationAnt.deployAnt();
                    if (newIntentionAnt.deployAnt(iPlan)) {
                        setupCharging(iPlan);
                    }
                    */

                }
            }

            if (curr.isPresent()) {
                final boolean inCargo = pm.containerContains(this, curr.get());
                // sanity check: if it is not in our cargo AND it is also not on the
                // RoadModel, we cannot go to curr anymore.
                if (!inCargo && !rm.containsObject(curr.get())) {
                    curr = Optional.absent();
                } else if (inCargo) {
                    // if it is in cargo and there's juice in the battery, go to its destination
                    if (this.battery.getCurrentBatteryCapacity() > 0) {
                        MoveProgress moveDetails = rm.moveTo(this, curr.get().getDeliveryLocation(), time);
                        this.battery.discharge(moveDetails);

                        if (rm.getPosition(this).equals(curr.get().getDeliveryLocation())) {
                            // deliver when we arrive
                            pm.deliver(this, curr.get(), time);
                            numOfCustomersDelivered++;
                        }

                    } else {
                        removePassenger(time);
                        setTaxiMode(TaxiMode.CHARGING);
                        setupCharging();
                        rm.objectDischarged(this);
                    }

                } else {
                    // it is still available, go there as fast as possible
                    if (this.battery.getCurrentBatteryCapacity() > 0) {
                        MoveProgress moveDetails = rm.moveTo(this, curr.get(), time);
                        this.battery.discharge(moveDetails);

                        if (rm.equalPosition(this, curr.get())) {
                            // pickup customer
                            pm.pickup(this, curr.get(), time);
                            distTravelledPerTrip = 0.0;
                            numOfCustomersPickedUp++;
                        }
                    } else {
                        removePassenger(time);
                        setTaxiMode(TaxiMode.CHARGING);
                        setupCharging();
                        rm.objectDischarged(this);

                    }
                }
            }
        } else {
            battery.charge(time);
        }
    }

    private boolean isPickupNotPossible(RoadModel rm, Optional<Parcel> curr) {
        return this.battery.getPercentBatteryRemaining() < 10
                || (!curr.isPresent() && !(this.battery.getPercentBatteryRemaining() > 50))
                || rm.getDistanceOfPath(
                rm.getShortestPathTo(rm.getPosition(this), curr.get().getPickupLocation()))
                .getValue() > this.battery.getCurrentBatteryCapacity();
    }


    private void removePassenger(TimeLapse time) {
        if (this.curr.isPresent()) {
            getPDPModel().removeParcel(this, curr.get(), time);
        }
    }

    /**
     * This method sets the respawn location once a taxi has been totally discharged.
     */
    private void setupCharging() {
        final RoadModel rm = getRoadModel();
        this.chargingLocation = rm.getRandomPosition(Simulator.getRandomGenerator());
    }

    /**
     * This method sets up taxiMode using an approved IntentionPlan
     *
     * @param iPlan
     */
    //TODO - Setup taxiMode and moving to the taxiMode station
    private void setupCharging(IntentionPlan iPlan) {
    }

    /**
     * This method stores the location at which the taxi is currently taxiMode.
     *
     * @param location Charging point location
     */
    private void setupCharging(Point location) {
        this.chargingLocation = location;
    }

    @Override
    public void batteryCharged() {
        final RoadModel rm = getRoadModel();
        rm.addObjectAt(this, this.chargingLocation);
        this.taxiMode = TaxiMode.IN_SERVICE;
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

        //Comment above code, uncomment this and modify the combineReport method to allow for an ant to be sent to multiple nearest nodes.
//        HashSet<ExplorationReport> explorationReports = new HashSet<ExplorationReport>();
//        for(Candidate candidate : this.otherCandidates){
//            TaxiExplorationAnt explorationAnt = new TaxiExplorationAnt(DEFAULT_EXPLORATION_ANT_LIFETIME, curBatteryCapacity, curPosition);
//            ExplorationReport plan = candidate.deployTaxiExplorationAnt(explorationAnt);
//            explorationReports.add(plan);
//        }
//        ExplorationReport combinedexplorationReport = combineReports(explorationReports);
    }

    private Candidate getClosestCandidate() {
        final RoadModel rm = getRoadModel();
        ArrayList<Candidate> allCandidates = new ArrayList<Candidate>(rm.getObjectsOfType(Candidate.class));

        allCandidates.sort((Candidate c1, Candidate c2) -> (int) (rm.getDistanceOfPath(rm.getShortestPathTo(this, c1.getPosition())).getValue() - rm.getDistanceOfPath(rm.getShortestPathTo(this, c2.getPosition())).getValue()));
        return allCandidates.get(0);
    }

    private ExplorationReport combineReports(HashSet<ExplorationReport> explorationReports) {
        //Not used right now.
        return null;
    }

    private void sendIntentionAnt(UUID targetCandidateId) {
        TaxiIntentionAnt intentionAnt = new TaxiIntentionAnt(this.ID, targetCandidateId, DEFAULT_INTENTION_PHEROMONE_LIFETIME);
        for (Candidate candidate : getRoadModel().getObjectsOfType(Candidate.class)) {
            // TODO
        }
    }

    /***
     * use this method to determine the best intention plan given a hashset of intention plans
     * @param explorationReport - the report discovered by the ants in this step
     * @return best intention plan that the taxi should adhere to
     */
    private IntentionPlan chooseBestPlan(ExplorationReport explorationReport) {
        //TODO
        return null;
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
        CHARGING
    }

}

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

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a very simple taxi agent. It moves to the closest customer,
 * picks it up, then delivers it, repeat.
 *
 * @author Rinde van Lon
 */
class Taxi extends Vehicle implements BatteryTaxiInterface {
    private int DEFAULT_EXPLORATION_ANT_LIFETIME = 2; // denotes how many nodes ants can travel sent by this taxi agent
    private int DEFAULT_INTENTION_PHEROMONE_LIFETIME = 100; // number of ticks an intention pheromone will last until it evaporates
    private static final double SPEED = 1000d;
    private Optional<Parcel> curr;
    private AgentBattery battery;
    private boolean charging;
    private Point chargingLocation;
    private double distTravelledPerTrip = 0.0;
    private Set<Candidate> otherCandidates; // denotes nodes that the taxi agent is able to send ants to

    // statistics
    private int numberOfCustomersTransported = 0;
    private int numberOfBatteryRunOuts = 0;

    Taxi(Point startPosition, int capacity, AgentBattery battery) {
        super(VehicleDTO.builder()
                .capacity(capacity)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());
        this.battery = battery;
        this.charging = false;
        curr = Optional.absent();
        this.battery.setParentTaxi(this);
    }

    public boolean isCharging() {
        return charging;
    }

    /**
     * This denotes the current status of the Taxi. If true, the taxi will not be aailable to pick customers.
     * If false, the taxi will be active.
     *
     * @param charging
     */
    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {
    }

    @Override
    protected void tickImpl(TimeLapse time) {
        if (!charging) {
            final RoadModel rm = getRoadModel();
            final PDPModel pm = getPDPModel();

            if (!time.hasTimeLeft()) {
                return;
            }
            if (!curr.isPresent()) {
                curr = Optional.fromNullable(RoadModels.findClosestObject(
                        rm.getPosition(this), rm, Parcel.class));

                if (isPickupPossible(rm, curr)) {
                    // Ant newExplorationAnt = new TaxiExplorationAnt();
                    IntentionPlan iPlan = sendExplorationAnts(battery.getCurrentBatteryCapacity(), rm.getPosition(this));
                    // Ant newIntentionAnt = new TaxiIntentionAnt();
                    // TODO: targetCandidateId = iPlan.getTargetNode();
                    String targetCandidateId = null;
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
                        }

                    } else {
                        removePassenger(time);
                        setCharging(true);
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
                        }
                    } else {
                        removePassenger(time);
                        setCharging(true);
                        setupCharging();
                        rm.objectDischarged(this);

                    }
                }
            }
        } else {
            battery.charge(time);
        }
    }

    private boolean isPickupPossible(RoadModel rm, Optional<Parcel> curr) {
        return this.battery.getPercentBatteryRemaining() < 10
                || (!curr.isPresent() && !(this.battery.getPercentBatteryRemaining() > 50))
                || rm.getDistanceOfPath(
                rm.getShortestPathTo(rm.getPosition(this), curr.get().getPickupLocation()))
                .getValue() > this.battery.getCurrentBatteryCapacity();
    }


    private void removePassenger(TimeLapse time) {
        numberOfCustomersTransported++;
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
     * This method sets up charging using an approved IntentionPlan
     *
     * @param iPlan
     */
    //TODO - Setup charging and moving to the charging station
    private void setupCharging(IntentionPlan iPlan) {
    }

    /**
     * This method stores the location at which the taxi is currently charging.
     *
     * @param location Charging point location
     */
    private void setupCharging(Point location) {
        this.chargingLocation = location;
    }

    @Override
    public void batteryCharged() {
        final RoadModel rm = getRoadModel();

        this.charging = false;
        rm.addObjectAt(this, this.chargingLocation);

    }

    /***
     * use this method to send exploration ants to all nodes the taxi is aware of
     * @param curBatteryCapacity
     * @param curPosition
     * @return Intention plan for taxi
     */
    private IntentionPlan sendExplorationAnts(double curBatteryCapacity, Point curPosition){
        // TODO: implement this process concurrently and tick-based in a later stage (if time allows it)
        HashSet<IntentionPlan> intentionPlans = new HashSet<IntentionPlan>();
        for(Candidate candidate : this.otherCandidates){
            TaxiExplorationAnt explorationAnt = new TaxiExplorationAnt(DEFAULT_EXPLORATION_ANT_LIFETIME, curBatteryCapacity, curPosition);
            IntentionPlan plan = candidate.deployTaxiExplorationAnt(explorationAnt);
            intentionPlans.add(plan);
        }
        return chooseBestIntentionPlan(intentionPlans);
    }

    private void sendIntentionAnt(String targetCandidateId){
        TaxiIntentionAnt intentionAnt = new TaxiIntentionAnt(targetCandidateId, DEFAULT_INTENTION_PHEROMONE_LIFETIME);
        for(Candidate candidate : this.otherCandidates){
            // TODO
        }
    }

    /***
     * use this method to determine the best intention plan given a hashset of intention plans
     * @param intentionPlans - the intention plans discovered by the ants in this step
     * @return best intention plan that the taxi should adhere to
     */
    private IntentionPlan chooseBestIntentionPlan(HashSet<IntentionPlan> intentionPlans){
        for(IntentionPlan intentionPlan : intentionPlans){
            // TODO
        }
        return null;
    }


}

import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Depot;
import core.model.road.GraphRoadModel;
import core.model.road.RoadModel;
import core.model.road.RoadUser;

import javax.measure.Measure;
import javax.measure.quantity.Length;
import java.util.*;

public class Candidate extends Depot {
    private UUID uniqueID;
    private PheromoneInfrastructure pheromoneInfrastructure;
    private Point position;
    private boolean chargingAgentAvailable = false;
    private ChargingAgentTaxiInterface chargingAgent;
    private Set<Candidate> otherCandidates;
    private int waitingSpots;
    private ArrayList<Taxi> waitingTaxis;


    Candidate(PheromoneInfrastructure pheromoneInfrastructure, Point position, int waitingSpots, UUID ID) {
        super(position);
        this.pheromoneInfrastructure = pheromoneInfrastructure;
        this.position = position;
        this.waitingSpots = waitingSpots;
        this.waitingTaxis = new ArrayList<Taxi>();
        this.uniqueID = ID;
    }


    public PheromoneInfrastructure getPheromoneInfrastructure() {
        return pheromoneInfrastructure;
    }

    public Point getPosition() {
        return position;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public Set<Candidate> getOtherCandidates() {
        return this.otherCandidates;
    }

    public void setOtherCandidates(Set<Candidate> otherCandidates) {
        this.otherCandidates = otherCandidates;
    }

    public void chargingAgentArrivesAtLocation(ChargingAgentTaxiInterface chargingAgent) {
        chargingAgentAvailable = true;
        this.chargingAgent = chargingAgent;
    }

    public void chargingAgentLeavesLocation() {
        chargingAgentAvailable = false;
        this.chargingAgent = null;
    }

    public void taxiJoinsWaitingQueue(Taxi taxi) {
        assert waitingSpots > 0;

        pheromoneInfrastructure.removeTaxiIntentionPheromone(taxi.getID());
        waitingSpots--;
        waitingTaxis.add(taxi);

    }

    public void taxiLeavesWaitingQueue(Taxi taxi) {
        waitingSpots++;
        waitingTaxis.remove(taxi);
    }

    public boolean isChargingAgentAvailable() {
        return chargingAgentAvailable;
    }

    public ChargingAgentTaxiInterface getChargingAgent() {
        return chargingAgent;
    }

    /***
     * deploys ant at a given candidate and returns exploration report.
     * @param ant
     * @return best intention plan for candidate
     */
    public ExplorationReport deployTaxiExplorationAnt(TaxiExplorationAnt ant) {

        //Generate empty exploration report
        ExplorationReport report = new ExplorationReport(ant.getOwnerId());

        //Visit other neighbouring nodes if the ant is allowed to replicate.
        if (ant.getNumAntGenerations() > 0) {
            report.mergeReports(visitOtherNodes(ant));
        }

        //Get strength of pheromone based on battery capacity of sender and drop pheromone accordingly.
        double strength = ant.calculateStrengthOfPheromone(this);
        pheromoneInfrastructure.dropPheromone(ant.getOwnerId(), new TaxiExplorationPheromone(ant.getPheromoneLifetime(), ant.getOwnerId(), strength));

        //Sniff available data on Node.
        TaxiCandidateData taxiCandidateData = getDataForTaxiFromCandidate();

        report.putReportEntry(this, taxiCandidateData);

        return report;

    }

    /***
     * send replicas to other nodes to check on best intention plan
     * @return
     */
    private ExplorationReport visitOtherNodes(TaxiExplorationAnt ant) {

        //Generate empty exploration report
        ExplorationReport report = new ExplorationReport(ant.getOwnerId());

        for(Candidate c : otherCandidates) {
            TaxiExplorationAnt explorationAnt =
                    new TaxiExplorationAnt(ant.getOwnerId(), ant.getNumAntGenerations() - 1,
                            ant.getCurrentBatteryPercent(), ant.getCurrentSpotOfAgent(), ant.getRange());
            report.mergeReports(c.deployTaxiExplorationAnt(explorationAnt));
        }
        return report;
    }

    private TaxiCandidateData getDataForTaxiFromCandidate() {
        boolean chargingAgentIntentionPresent = false;
        double expectedWaitingTime = 0d;
        if (chargingAgentAvailable) {
            expectedWaitingTime = 0d;
        } else {
            if (!pheromoneInfrastructure.getChargeIntentionPheromoneDetails().isEmpty()) {
                chargingAgentIntentionPresent = true;

                expectedWaitingTime = pheromoneInfrastructure.getChargeIntentionPheromoneDetails()
                        .values().stream().findFirst().get().getLifeTime();
            }
        }
        boolean reservationsPresent = false;
        boolean waitingSpotsAvailable = true;
        Map<UUID, TaxiIntentionPheromone> taxiIntentionPheromonesOnDeployedNode
                = pheromoneInfrastructure.getTaxiIntentionPheromoneDetails();
        if (!taxiIntentionPheromonesOnDeployedNode.isEmpty()) {
            reservationsPresent = true;
            if (waitingSpots - taxiIntentionPheromonesOnDeployedNode.size() == 0) {
                waitingSpotsAvailable = false;
            } else {
                for (Map.Entry<UUID, TaxiIntentionPheromone> entry
                        : taxiIntentionPheromonesOnDeployedNode.entrySet()) {
                    Optional<Taxi> retrievedTaxi = waitingTaxis.stream()
                            .filter(taxi -> taxi.getID().equals(entry.getKey()))
                            .findFirst();
                    if (retrievedTaxi.isPresent()) {
                        expectedWaitingTime += (entry.getValue().getLifeTime() +
                                retrievedTaxi.get().getExpectedChargingTime());
                    } else {
                        expectedWaitingTime += entry.getValue().getLifeTime();
                    }
                }
            }
        }

        TaxiCandidateData taxiCandidateData
                = new TaxiCandidateData(chargingAgentAvailable, chargingAgentIntentionPresent,
                reservationsPresent, waitingSpotsAvailable, expectedWaitingTime);

        return taxiCandidateData;
    }

    public boolean sendChargingIntentionAntToLocation(ChargeIntentionAnt chargeIntentionAnt, Candidate bestCandidate) {

        if (!bestCandidate.getchargingAgentAvailable()) {
            boolean success = bestCandidate.getPheromoneInfrastructure().
                    dropPheromone(chargeIntentionAnt.getOwnerId(), new ChargeIntentionPheromone(chargeIntentionAnt.getPheromoneLifetime(), chargeIntentionAnt.getOwnerId()));
            return success;
        } else {
            return false;
        }
    }

    private boolean getchargingAgentAvailable() {
        return this.chargingAgentAvailable;
    }

    public double getDistanceFrom(Point targetPoint, Class<Taxi> taxiClass, UUID ownerId) {
        if (this.position == targetPoint)
            return 0;
        final RoadModel rm = getRoadModel();
        List<RoadUser> taxis = new ArrayList<RoadUser>(rm.getObjects((RoadUser r) -> r.getClass().isAssignableFrom(taxiClass)
                && ((Taxi) r).getID().equals(ownerId)));
        return getDistanceOfPath(rm, this, (Taxi) taxis.get(0));
    }

    public boolean deployTaxiIntentionAnt(TaxiIntentionAnt taxiIntentionAnt) {

        Candidate bestCandidate = taxiIntentionAnt.getIntentionPlan().getChosenCandidate();
        if (bestCandidate.waitingSpots - bestCandidate.getPheromoneInfrastructure()
                .getTaxiIntentionPheromoneDetails().size() > 0)
            return bestCandidate.getPheromoneInfrastructure().dropPheromone(taxiIntentionAnt.getOwnerId(),
                    new TaxiIntentionPheromone(taxiIntentionAnt.getPheromoneLifetime(), taxiIntentionAnt.getOwnerId()));
        else
            return false;

    }

    public Taxi getTaxiFromWaitingList() {
        Taxi taxi = null;
        if (waitingTaxis.size() > 0)
            taxi = waitingTaxis.get(0);
        taxiLeavesWaitingQueue(taxi);
        return taxi;
    }

    public boolean areTaxisWaiting() {
        return waitingTaxis.size() > 0;
    }

    private double getDistanceOfPath(RoadModel rm, Candidate start, Taxi taxi) {
        com.google.common.base.Optional<? extends Connection<?>> conn = ((GraphRoadModel) rm).getConnection(taxi);
        Point to;
        double dist = 0;
        if (conn.isPresent()) {
            dist += Point.distance(rm.getPosition(taxi), conn.get().to());
            to = conn.get().to();
        } else {
            to = rm.getPosition(taxi);
        }

        List<Point> path = getRoadModel().getShortestPathTo(rm.getPosition(this), to);
        Measure<Double, Length> distance = rm.getDistanceOfPath(path);
        // total distance is the sum of distance and dist

        return dist + distance.getValue();
    }
}

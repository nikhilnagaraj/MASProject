import com.github.rinde.rinsim.geom.Point;
import core.model.pdp.Depot;

import java.util.*;

public class Candidate extends Depot {
    private UUID uniqueID;
    private PheromoneInfrastructure pheromoneInfrastructure;
    private Point position;
    private boolean chargingAgentAvailable = false;
    private Set<Candidate> otherCandidates;
    private int waitingSpots;
    private ArrayList<Taxi> waitingTaxis;


    Candidate(PheromoneInfrastructure pheromoneInfrastructure, Point position, int waitingSpots) {
        super(position);
        this.pheromoneInfrastructure = pheromoneInfrastructure;
        this.position = position;
        this.waitingSpots = waitingSpots;
        uniqueID = UUID.randomUUID();
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

    public void chargingAgentArrivesAtLocation() {
        chargingAgentAvailable = true;
    }

    public void chargingAgentLeavesLocation() {
        chargingAgentAvailable = false;
    }

    public void taxiJoinsWaitingQueue(Taxi taxi) {
        waitingSpots--;
        waitingTaxis.add(taxi);
    }

    public void taxiLeavesWaitingQueue(Taxi taxi) {
        waitingSpots++;
        waitingTaxis.remove(taxi);
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
        double strength = ant.calculateStrengthOfPheromone();
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
                            ant.getCurrentBatteryCapacity(), ant.getCurrentSpotOfAgent());
            report.mergeReports(c.deployTaxiExplorationAnt(explorationAnt));
        }
        return report;
    }

    private TaxiCandidateData getDataForTaxiFromCandidate() {
        boolean chargingAgentIntentionPresent = false;
        double expectedWaitingTime;
        if (chargingAgentAvailable) {
            expectedWaitingTime = 0d;
        } else {
            if (!pheromoneInfrastructure.getChargeIntentionPheromoneDetails().isEmpty()) {
                chargingAgentIntentionPresent = true;
            }
            expectedWaitingTime = pheromoneInfrastructure.getChargeIntentionPheromoneDetails()
                    .values().stream().findFirst().get().getLifeTime();
        }

        boolean reservationsPresent = false;
        boolean waitingSpotsAvailable = false;
        Map<UUID, TaxiIntentionPheromone> taxiIntentionPheromonesOnDeployedNode
                = pheromoneInfrastructure.getTaxiIntentionPheromoneDetails();
        if (!taxiIntentionPheromonesOnDeployedNode.isEmpty()) {
            reservationsPresent = true;
            if (waitingSpots > 0) {
                waitingSpotsAvailable = true;
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
}

import core.model.time.TickListener;
import core.model.time.TimeLapse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PheromoneInfrastructure implements TickListener {


    /***
     * The pheromone infrastructure is deployed on candidate nodes and used by ants to interact with each other through pheromones.
     */
    private Map<UUID, TaxiExplorationPheromone> taxiExplorationPheromones
            = new HashMap<UUID, TaxiExplorationPheromone>();
    private Map<UUID, TaxiIntentionPheromone> taxiIntentionPheromones
            = new HashMap<UUID, TaxiIntentionPheromone>();
    private Map<UUID, ChargeIntentionPheromone> chargeIntentionPheromones
            = new HashMap<UUID, ChargeIntentionPheromone>();
    private UUID ID;

    public PheromoneInfrastructure(UUID ID) {
        this.ID = ID;
    }

    /***
     * Renew the pheromone dropped by an ant that belongs to owner with given ownerId
     * @param ownerId
     * @param pheromone
     */
    public void dropPheromone(UUID ownerId, TaxiExplorationPheromone pheromone) {
        taxiExplorationPheromones.remove(ownerId);
        taxiExplorationPheromones.put(ownerId,pheromone);
    }

    /***
     * Renew the pheromone dropped by an ant that belongs to owner with given ownerId
     * @param ownerId
     * @param pheromone
     */
    public boolean dropPheromone(UUID ownerId, TaxiIntentionPheromone pheromone) {
        taxiIntentionPheromones.remove(ownerId);
        taxiIntentionPheromones.put(ownerId,pheromone);
        return true;
    }

    /***
     * Renew the pheromone dropped by an ant that belongs to owner with given ownerId
     * @param ownerId
     * @param pheromone
     */
    public boolean dropPheromone(UUID ownerId, ChargeIntentionPheromone pheromone) {
        chargeIntentionPheromones.remove(ownerId);
        if (chargeIntentionPheromones.isEmpty()) {
            chargeIntentionPheromones.put(ownerId, pheromone);
            return true;
        }
        return false;
    }

    public Map<UUID, TaxiExplorationPheromone> getTaxiExplorationPheromoneDetails() {
        return this.taxiExplorationPheromones;
    }

    public Map<UUID, TaxiIntentionPheromone> getTaxiIntentionPheromoneDetails() {
        return this.taxiIntentionPheromones;
    }

    public Map<UUID, ChargeIntentionPheromone> getChargeIntentionPheromoneDetails() {
        return this.chargeIntentionPheromones;
    }

    /**
     * Used by ChargeExplorationAnts to find out the total strength of the taxi pheromones at a particular
     * candidate.
     *
     * @return
     */
    public double smellTaxiExplorationPheromones() {
        double totalStrength = 0.0;
        for (TaxiExplorationPheromone pheromone : taxiExplorationPheromones.values()) {
            totalStrength += pheromone.getStrength();
        }

        return totalStrength;
    }

    /***
     * decrease life time of every pheromone and check if it needs to be removed
     * @param timeLapse The time lapse that is handed to this object.
     */
    @Override
    public void tick(TimeLapse timeLapse) {

        Map<UUID, TaxiExplorationPheromone> newTaxiExplorationPheromones = new HashMap<UUID, TaxiExplorationPheromone>();
        for (Map.Entry<UUID, TaxiExplorationPheromone> entry : taxiExplorationPheromones.entrySet()) {
            entry.getValue().decrementLifeTime();
            if (!entry.getValue().isEvaporated())
                newTaxiExplorationPheromones.put(entry.getKey(), entry.getValue());
        }
        this.taxiExplorationPheromones = newTaxiExplorationPheromones;

        Map<UUID, TaxiIntentionPheromone> newTaxiIntentionPheromones = new HashMap<UUID, TaxiIntentionPheromone>();
        for (Map.Entry<UUID, TaxiIntentionPheromone> entry : taxiIntentionPheromones.entrySet()) {
            entry.getValue().decrementLifeTime();
            if (!entry.getValue().isEvaporated())
                newTaxiIntentionPheromones.put(entry.getKey(), entry.getValue());
        }
        this.taxiIntentionPheromones = newTaxiIntentionPheromones;

        Map<UUID, ChargeIntentionPheromone> newChargeIntentionPheromones = new HashMap<UUID, ChargeIntentionPheromone>();
        for (Map.Entry<UUID, ChargeIntentionPheromone> entry : chargeIntentionPheromones.entrySet()) {
            entry.getValue().decrementLifeTime();
            if (!entry.getValue().isEvaporated())
                newChargeIntentionPheromones.put(entry.getKey(), entry.getValue());
        }
        this.chargeIntentionPheromones = newChargeIntentionPheromones;
    }

    @Override
    public void afterTick(TimeLapse timeLapse) { }

    public void removeTaxiIntentionPheromone(UUID id) {
        taxiIntentionPheromones.remove(id);
    }

    public void removeChargingIntentionPheromone(UUID id) {
        chargeIntentionPheromones.remove(id);
    }
}

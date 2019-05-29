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

    /***
     * Renew the pheromone dropped by an ant that belongs to owner with given ownerId
     * @param ownerId
     * @param pheromone
     */
    public void dropPheromone(UUID ownerId, TaxiExplorationPheromone pheromone) {
        taxiExplorationPheromones.entrySet().removeIf(entry -> (ownerId.equals(entry.getKey())));
        taxiExplorationPheromones.put(ownerId,pheromone);
    }

    /***
     * Renew the pheromone dropped by an ant that belongs to owner with given ownerId
     * @param ownerId
     * @param pheromone
     */
    public boolean dropPheromone(UUID ownerId, TaxiIntentionPheromone pheromone) {
        taxiIntentionPheromones.entrySet().removeIf(entry -> (ownerId.equals(entry.getKey())));
        taxiIntentionPheromones.put(ownerId,pheromone);
        return true;
    }

    /***
     * Renew the pheromone dropped by an ant that belongs to owner with given ownerId
     * @param ownerId
     * @param pheromone
     */
    public boolean dropPheromone(UUID ownerId, ChargeIntentionPheromone pheromone) {
        chargeIntentionPheromones.entrySet().removeIf(entry -> (ownerId.equals(entry.getKey())));
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
        for(TaxiExplorationPheromone pheromone : taxiExplorationPheromones.values()){
            pheromone.decrementLifeTime();
            if(pheromone.isEvaporated()){
                taxiExplorationPheromones.remove(pheromone);
            }
        }
        for(TaxiIntentionPheromone pheromone : taxiIntentionPheromones.values()){
            pheromone.decrementLifeTime();
            if(pheromone.isEvaporated()){
                taxiIntentionPheromones.remove(pheromone);
            }
        }
        for(ChargeIntentionPheromone pheromone : chargeIntentionPheromones.values()){
            pheromone.decrementLifeTime();
            if(pheromone.isEvaporated()){
                chargeIntentionPheromones.remove(pheromone);
            }
        }
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

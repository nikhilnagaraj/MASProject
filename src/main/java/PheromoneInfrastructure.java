import core.model.time.TickListener;
import core.model.time.TimeLapse;
import java.util.Map;

public class PheromoneInfrastructure implements TickListener {
    /***
     * The pheromone infrastructure is deployed on candidate nodes and used by ants to interact with each other through pheromones.
     */
    private Map<String, TaxiExplorationPheromone> taxiExplorationPheromones;
    private Map<String, TaxiIntentionPheromone> taxiIntentionPheromones;
    private Map<String, ChargeIntentionPheromone> chargeIntentionPheromones;

    /***
     * Renew the pheromone dropped by an ant that belongs to owner with given ownerId
     * @param ownerId
     * @param pheromone
     */
    public void dropPheromone(String ownerId, TaxiExplorationPheromone pheromone){
        taxiExplorationPheromones.entrySet().removeIf(entry -> (ownerId == entry.getKey()));
        taxiExplorationPheromones.put(ownerId,pheromone);
    }

    /***
     * Renew the pheromone dropped by an ant that belongs to owner with given ownerId
     * @param ownerId
     * @param pheromone
     */
    public void dropPheromone(String ownerId, TaxiIntentionPheromone pheromone){
        taxiIntentionPheromones.entrySet().removeIf(entry -> (ownerId == entry.getKey()));
        taxiIntentionPheromones.put(ownerId,pheromone);
    }

    /***
     * Renew the pheromone dropped by an ant that belongs to owner with given ownerId
     * @param ownerId
     * @param pheromone
     */
    public void dropPheromone(String ownerId, ChargeIntentionPheromone pheromone){
        chargeIntentionPheromones.entrySet().removeIf(entry -> (ownerId == entry.getKey()));
        chargeIntentionPheromones.put(ownerId,pheromone);
    }

    public Map<String, TaxiExplorationPheromone> smellForTaxiExplorationPheromones(){
        return this.taxiExplorationPheromones;
    }

    public Map<String, TaxiIntentionPheromone> smellForTaxiIntentionPheromones(){
        return this.taxiIntentionPheromones;
    }

    public Map<String, ChargeIntentionPheromone> smellForChargeIntentionPheromones(){
        return this.chargeIntentionPheromones;
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
}

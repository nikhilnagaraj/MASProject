import core.model.road.MoveProgress;
import core.model.time.TimeLapse;


/**
 * Denotes the battery in an agent.
 */
public class AgentBattery {

    private final double totalBatteryCapacity;
    private final double rateOfCharge;
    private double currentBatteryCapacity;
    private BatteryTaxiInterface parentTaxi;

    public AgentBattery(double totalBatteryCapacity, double rateOfCharge) {
        /**
         * Constructs a battery object. The capacity of the battery is denoted
         * by the total distance the vehicle an travel.
         *
         * @param Total distance the agent can travel on a full charge.
         * @param The rate at which the battery charges.
         */
        this.totalBatteryCapacity = totalBatteryCapacity;
        this.currentBatteryCapacity = totalBatteryCapacity;
        this.rateOfCharge = rateOfCharge;
    }

    public void setParentTaxi(BatteryTaxiInterface parentTaxi) {
        this.parentTaxi = parentTaxi;
    }

    public double getTotalBatteryCapacity() {
        /**
         * Returns the total capacity of the battery.
         *
         * @return The total capacity of the battery.
         */
        return totalBatteryCapacity;
    }

    public double getCurrentBatteryCapacity() {
        /**
         * Returns the current capacity of the battery.
         *
         * @return The current capacity of the battery.
         */
        return currentBatteryCapacity;
    }

    public double getPercentBatteryRemaining() {
        /**
         * Returns the amount of battery remaining in terms of the total
         * capacity of the battery.
         */
        return (this.currentBatteryCapacity / this.totalBatteryCapacity) * 100;
    }

    public void discharge(MoveProgress moveDetails) {
        /**
         * Discharges the battery in accordance with the distance travelled.
         *
         * @param A {@link MoveProgress} instance which details: the distance
         *       traveled, the actual time spent traveling and the nodes which where
         *       traveled.
         */

        if (moveDetails.distance().getValue() > this.currentBatteryCapacity) {
            this.currentBatteryCapacity = 0;
        } else {
            this.currentBatteryCapacity -= moveDetails.distance().getValue();
        }
    }

    public void charge(TimeLapse time) {
        /**
         * Charges the battery in accordance with the time spent at the charging station.
         */
        this.currentBatteryCapacity += time.getTickLength() * this.rateOfCharge;
        if (this.currentBatteryCapacity >= this.totalBatteryCapacity) {
            this.currentBatteryCapacity = this.totalBatteryCapacity;
            parentTaxi.batteryCharged();
        }
    }

    @Override
    public String toString() {

        String batteryInfo = String.format("This battery can travel a distance of %f meters when fully charged." +
                        "At this moment, the battery can travel a distance of %f meters before dying out." +
                        "The battery charges at a rate of %f per second.", this.totalBatteryCapacity, this.currentBatteryCapacity,
                this.rateOfCharge);

        return batteryInfo;
    }
}

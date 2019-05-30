class TaxiCandidateData {

    private final boolean chargingAgentPresent;
    private final boolean chargingAgentIntentionPresent;
    private final boolean reservationsPresent;
    private final boolean waitingSpotsAvailable;
    private final double expectedWaitingTime;


    TaxiCandidateData(boolean chargingAgentPresent, boolean chargingAgentIntentionPresent,
                      boolean reservationsPresent,
                      boolean waitingSpotsAvailable, double expectedWaitingTime) {
        this.chargingAgentPresent = chargingAgentPresent;
        this.chargingAgentIntentionPresent = chargingAgentIntentionPresent;
        this.reservationsPresent = reservationsPresent;
        this.waitingSpotsAvailable = waitingSpotsAvailable;
        this.expectedWaitingTime = expectedWaitingTime;
    }

    public boolean isChargingAgentPresent() {
        return this.chargingAgentPresent;
    }

    public boolean isChargingAgentIntentionPresent() {
        return this.chargingAgentIntentionPresent;
    }

    public boolean isReservationsPresent() {
        return reservationsPresent;
    }

    public boolean isWaitingSpotsAvailable() {
        return waitingSpotsAvailable;
    }

    public double getExpectedWaitingTime() {
        return expectedWaitingTime;
    }
}
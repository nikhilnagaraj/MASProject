import java.util.UUID;

public abstract class Ant{
    private UUID ownerId;

    public Ant(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

}

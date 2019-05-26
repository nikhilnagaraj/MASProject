
package core.model.comm;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

final class AutoValue_Message extends Message {

    private final CommUser from;
    private final Optional<CommUser> to;
    private final MessageContents contents;
    private final Predicate<CommUser> predicate;

    AutoValue_Message(
            CommUser from,
            Optional<CommUser> to,
            MessageContents contents,
            Predicate<CommUser> predicate) {
        if (from == null) {
            throw new NullPointerException("Null from");
        }
        this.from = from;
        if (to == null) {
            throw new NullPointerException("Null to");
        }
        this.to = to;
        if (contents == null) {
            throw new NullPointerException("Null contents");
        }
        this.contents = contents;
        if (predicate == null) {
            throw new NullPointerException("Null predicate");
        }
        this.predicate = predicate;
    }

    @Override
    CommUser from() {
        return from;
    }

    @Override
    Optional<CommUser> to() {
        return to;
    }

    @Override
    MessageContents contents() {
        return contents;
    }

    @Override
    Predicate<CommUser> predicate() {
        return predicate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Message) {
            Message that = (Message) o;
            return (this.from.equals(that.from()))
                    && (this.to.equals(that.to()))
                    && (this.contents.equals(that.contents()))
                    && (this.predicate.equals(that.predicate()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.from.hashCode();
        h *= 1000003;
        h ^= this.to.hashCode();
        h *= 1000003;
        h ^= this.contents.hashCode();
        h *= 1000003;
        h ^= this.predicate.hashCode();
        return h;
    }

}

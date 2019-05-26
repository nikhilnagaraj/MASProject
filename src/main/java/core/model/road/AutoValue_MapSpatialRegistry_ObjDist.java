
package core.model.road;

final class AutoValue_MapSpatialRegistry_ObjDist<T> extends MapSpatialRegistry.ObjDist<T> {

    private final T obj;
    private final double dist;

    AutoValue_MapSpatialRegistry_ObjDist(
            T obj,
            double dist) {
        if (obj == null) {
            throw new NullPointerException("Null obj");
        }
        this.obj = obj;
        this.dist = dist;
    }

    @Override
    T obj() {
        return obj;
    }

    @Override
    double dist() {
        return dist;
    }

    @Override
    public String toString() {
        return "ObjDist{"
                + "obj=" + obj + ", "
                + "dist=" + dist
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MapSpatialRegistry.ObjDist) {
            MapSpatialRegistry.ObjDist<?> that = (MapSpatialRegistry.ObjDist<?>) o;
            return (this.obj.equals(that.obj()))
                    && (Double.doubleToLongBits(this.dist) == Double.doubleToLongBits(that.dist()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.obj.hashCode();
        h *= 1000003;
        h ^= (Double.doubleToLongBits(this.dist) >>> 32) ^ Double.doubleToLongBits(this.dist);
        return h;
    }

}

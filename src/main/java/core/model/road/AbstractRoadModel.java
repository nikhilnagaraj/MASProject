/*
 * Copyright (C) 2011-2018 Rinde R.S. van Lon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package core.model.road;

import com.github.rinde.rinsim.event.EventAPI;
import com.github.rinde.rinsim.geom.GeomHeuristic;
import com.github.rinde.rinsim.geom.GeomHeuristics;
import com.github.rinde.rinsim.geom.Point;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import core.model.time.TimeLapse;

import javax.annotation.Nullable;
import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Verify.verifyNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;

/**
 * A common space neutral implementation of {@link RoadModel}. It implements a
 * data structure for managing objects and locations and checks many
 * preconditions as defined in {@link RoadModel}.
 *
 * @author Rinde van Lon
 */
public abstract class AbstractRoadModel extends GenericRoadModel {

    /**
     * A reference to {@link RoadUnits}.
     */
    protected final RoadUnits unitConversion;
    /**
     * A mapping of {@link MovingRoadUser}s to {@link DestinationPath}s.
     */
    protected Map<MovingRoadUser, DestinationPath> objDestinations;

    /**
     * Create a new instance.
     *
     * @param distanceUnit The distance unit used to interpret all supplied
     *                     distances.
     * @param speedUnit    The speed unit used to interpret all supplied speeds.
     */
    protected AbstractRoadModel(Unit<Length> distanceUnit,
                                Unit<Velocity> speedUnit) {
        super();
        unitConversion = new RoadUnits(distanceUnit, speedUnit);
        objDestinations = newLinkedHashMap();
    }

    /**
     * @return A mapping of {@link RoadUser} to location.
     */
    protected abstract SpatialRegistry<RoadUser> registry();

    @Override
    public MoveProgress followPath(MovingRoadUser object, Queue<Point> path,
                                   TimeLapse time) {
        checkArgument(registry().containsObject(object),
                "Object: %s must have a location.", object);
        checkArgument(!path.isEmpty(),
                "Path can not be empty, found empty path for %s.", object);
        checkArgument(time.hasTimeLeft(),
                "Can not follow path when no time is left. For road user %s.", object);
        final Point dest = newArrayList(path).get(path.size() - 1);
        objDestinations.put(object, new DestinationPath(dest, path));
        final MoveProgress mp = doFollowPath(object, path, time);
        eventDispatcher.dispatchEvent(new MoveEvent(self, object, mp));
        return mp;
    }

    @Override
    public MoveProgress moveTo(MovingRoadUser object, Point destination,
                               TimeLapse time) {
        return moveTo(object, destination, time, GeomHeuristics.euclidean());
    }

    @Override
    public MoveProgress moveTo(MovingRoadUser object, RoadUser destination,
                               TimeLapse time) {
        return moveTo(object, getPosition(destination), time);
    }

    @Override
    public MoveProgress moveTo(MovingRoadUser object, RoadUser destination,
                               TimeLapse time, GeomHeuristic heuristic) {
        return moveTo(object, getPosition(destination), time, heuristic);
    }

    @Override
    public MoveProgress moveTo(MovingRoadUser object, Point destination,
                               TimeLapse time, GeomHeuristic heuristic) {
        final Queue<Point> path;
        if (objDestinations.containsKey(object)
                && objDestinations.get(object).destination.equals(destination)) {
            // is valid move? -> assume it is
            path = objDestinations.get(object).path;
        } else {
            path = new LinkedList<>(
                    getPathTo(object, destination, time.getTimeUnit(),
                            Measure.valueOf(unitConversion.toExSpeed(object.getSpeed()),
                                    unitConversion.getExSpeedUnit()),
                            heuristic).getPath());
            objDestinations.put(object, new DestinationPath(destination, path));
        }
        final MoveProgress mp = doFollowPath(object, path, time);
        eventDispatcher.dispatchEvent(new MoveEvent(self, object, mp));
        return mp;
    }

    /**
     * Should be overridden by subclasses to define actual
     * {@link RoadModel#followPath(MovingRoadUser, Queue, TimeLapse)} behavior.
     *
     * @param object The object that is moved.
     * @param path   The path that is followed.
     * @param time   The time that is available for travel.
     * @return A {@link MoveProgress} instance containing the actual travel
     * details.
     */
    protected abstract MoveProgress doFollowPath(MovingRoadUser object,
                                                 Queue<Point> path, TimeLapse time);

    @Override
    @Nullable
    public Point getDestination(MovingRoadUser object) {
        if (objDestinations.containsKey(object)) {
            return objDestinations.get(object).destination;
        }
        return null;
    }

    @Override
    public void addObjectAt(RoadUser newObj, Point pos) {
        checkArgument(!registry().containsObject(newObj),
                "Object is already added: %s.", newObj);
        registry().addAt(newObj, pos);
        eventDispatcher.dispatchEvent(new RoadModelEvent(
                RoadEventType.ADD_ROAD_USER, this, newObj));
    }

    @Override
    public void addObjectAtSamePosition(RoadUser newObj, RoadUser existingObj) {
        checkArgument(!registry().containsObject(newObj), "Object %s is already "
                + "added.", newObj);
        checkArgument(registry().containsObject(existingObj),
                "Object %s does not exist.", existingObj);
        registry().addAt(newObj, registry().getPosition(existingObj));
        eventDispatcher.dispatchEvent(new RoadModelEvent(
                RoadEventType.ADD_ROAD_USER, this, newObj));
    }


    @Override
    public void objectDischarged(RoadUser roadUser) {
        checkArgument(registry().containsObject(roadUser),
                "RoadUser %s does not exist.", roadUser);
        removeObject(roadUser);
    }

    @Override
    public void removeObject(RoadUser roadUser) {
        checkArgument(registry().containsObject(roadUser),
                "RoadUser: %s does not exist.", roadUser);
        registry().removeObject(roadUser);
        objDestinations.remove(roadUser);
        eventDispatcher.dispatchEvent(new RoadModelEvent(
                RoadEventType.REMOVE_ROAD_USER, this, roadUser));
    }

    @Override
    public void clear() {
        registry().clear();
        objDestinations.clear();
    }

    @Override
    public boolean containsObject(RoadUser obj) {
        return registry().containsObject(obj);
    }

    @Override
    public boolean containsObjectAt(RoadUser obj, Point p) {
        if (containsObject(obj)) {
            return registry().getPosition(obj).equals(p);
        }
        return false;
    }

    @Override
    public boolean equalPosition(RoadUser obj1, RoadUser obj2) {
        return containsObject(obj1) && containsObject(obj2)
                && getPosition(obj1).equals(getPosition(obj2));
    }

    @Override
    public ImmutableMap<RoadUser, Point> getObjectsAndPositions() {
        return registry().getObjectsAndPositions();
    }

    @Override
    public Point getPosition(RoadUser roadUser) {
        return registry().getPosition(roadUser);
    }

    @Override
    public ImmutableCollection<Point> getObjectPositions() {
        return getObjectsAndPositions().values();
    }

    @Override
    public ImmutableSet<RoadUser> getObjects() {
        return registry().getObjects();
    }

    @Override
    public Set<RoadUser> getObjects(Predicate<RoadUser> predicate) {
        return Sets.filter(getObjects(), predicate);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Y extends RoadUser> Set<Y> getObjectsAt(RoadUser roadUser,
                                                    Class<Y> type) {
        final Set<Y> result = new HashSet<>();
        for (final RoadUser ru : getObjects(new SameLocationPredicate(roadUser,
                type, self))) {
            result.add((Y) ru);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y extends RoadUser> Set<Y> getObjectsOfType(final Class<Y> type) {
        return (Set<Y>) getObjects(new Predicate<RoadUser>() {
            @Override
            public boolean apply(@Nullable RoadUser input) {
                return type.isInstance(input);
            }
        });
    }

    @Override
    public List<Point> getShortestPathTo(RoadUser fromObj, RoadUser toObj) {
        checkArgument(registry().containsObject(toObj),
                "To object (%s) should be in RoadModel.", toObj);
        return getShortestPathTo(fromObj, getPosition(toObj));
    }

    @Override
    public List<Point> getShortestPathTo(RoadUser fromObj, Point to) {
        checkArgument(registry().containsObject(fromObj),
                "From object (%s) should be in RoadModel.", fromObj);
        return getShortestPathTo(getPosition(fromObj), to);
    }

    @Override
    protected boolean doRegister(RoadUser roadUser) {
        LOGGER.info("register {}", roadUser);
        roadUser.initRoadUser(self);
        return true;
    }

    @Override
    public boolean unregister(RoadUser roadUser) {
        final boolean contains = containsObject(roadUser);
        LOGGER.info("unregister {} succes: {}", roadUser, contains);
        if (contains) {
            removeObject(roadUser);
            return true;
        }
        return false;
    }

    /**
     * See {@link RoadEventType} for the list of available event
     * types. {@inheritDoc}
     */
    @Override
    public final EventAPI getEventAPI() {
        return eventDispatcher.getPublicEventAPI();
    }

    @Override
    public Unit<Length> getDistanceUnit() {
        return unitConversion.getExDistUnit();
    }

    @Override
    public Unit<Velocity> getSpeedUnit() {
        return unitConversion.getExSpeedUnit();
    }

    @SuppressWarnings("null")
    private static class SameLocationPredicate implements Predicate<RoadUser> {
        private final RoadUser reference;
        private final RoadModel model;
        private final Class<?> type;

        SameLocationPredicate(final RoadUser pReference, final Class<?> pType,
                              final RoadModel pModel) {
            reference = pReference;
            type = pType;
            model = pModel;
        }

        @Override
        public boolean apply(@Nullable RoadUser input) {
            return type.isInstance(input)
                    && model.equalPosition(verifyNotNull(input), reference);
        }
    }

    /**
     * Simple class for storing destinations and paths leading to them.
     *
     * @author Rinde van Lon
     */
    protected class DestinationPath {
        /**
         * The destination of the path.
         */
        public final Point destination;
        /**
         * The path leading to the destination.
         */
        public final Queue<Point> path;

        DestinationPath(Point dest, Queue<Point> p) {
            destination = dest;
            path = p;
        }
    }
}

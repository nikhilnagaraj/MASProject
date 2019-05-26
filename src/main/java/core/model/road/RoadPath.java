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

import com.github.rinde.rinsim.geom.GeomHeuristic;
import com.github.rinde.rinsim.geom.Point;

import java.util.List;

/**
 * A value object containing a path with a certain heuristic value and travel
 * time. The heuristic value is the result of determining the value of the path
 * by a {@link GeomHeuristic}.
 *
 * @author Vincent Van Gestel
 */

public abstract class RoadPath {

    /**
     * Creates a new path annotated with its heuristic value and travel time.
     *
     * @param path       The actual path.
     * @param value      The heuristic value of the path.
     * @param travelTime Travel time that it takes to traverse the path as
     *                   calculated by the heuristic.
     * @return The annotated heuristic path.
     */
    public static RoadPath create(
            List<Point> path,
            double value,
            double travelTime) {
        return new AutoValue_RoadPath(path, value, travelTime);
    }

    /**
     * @return The actual path.
     */
    public abstract List<Point> getPath();

    /**
     * @return The heuristic value of the path.
     */
    public abstract double getValue();

    /**
     * @return Travel time that it takes to traverse the path as calculated by the
     * heuristic.
     */
    public abstract double getTravelTime();

}

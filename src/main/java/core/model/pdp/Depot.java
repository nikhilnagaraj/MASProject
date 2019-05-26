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
package core.model.pdp;

import com.github.rinde.rinsim.geom.Point;
import core.model.road.RoadModel;

import javax.annotation.Nullable;

/**
 * Abstract base class for depot concept: a stationary {@link Container}.
 *
 * @author Rinde van Lon
 */
public class Depot extends ContainerImpl {

    private final String name;

    /**
     * Instantiate the depot at the provided position.
     *
     * @param position The position where the depot will be located.
     */
    public Depot(Point position) {
        this(position, null);
    }

    /**
     * Instantiate the depot at the provided position.
     *
     * @param position The position where the depot will be located.
     * @param nm       The name of the depot, this overrides the toString value.
     */
    public Depot(Point position, @Nullable String nm) {
        setStartPosition(position);
        if (nm == null) {
            name = "";
        } else {
            name = nm;
        }
    }

    @Override
    public final PDPType getType() {
        return PDPType.DEPOT;
    }

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
    }

    @Override
    public String toString() {
        if (name.length() == 0) {
            return super.toString();
        }
        return name;
    }
}

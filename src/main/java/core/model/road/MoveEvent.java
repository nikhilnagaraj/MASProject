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

import core.model.road.GenericRoadModel.RoadEventType;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Event representing a move of a {@link MovingRoadUser}.
 *
 * @author Rinde van Lon
 */
public class MoveEvent extends RoadModelEvent {

    /**
     * Object containing the distance, time and path of this move.
     */
    public final MoveProgress pathProgress;

    MoveEvent(RoadModel rm, MovingRoadUser ru, MoveProgress pp) {
        super(RoadEventType.MOVE, rm, ru);
        pathProgress = pp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

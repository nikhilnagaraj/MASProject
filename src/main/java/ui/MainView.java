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
package ui;

import com.github.rinde.rinsim.event.Listener;

/**
 * Implementations should represent the main view of an UI application.
 *
 * @author Rinde van Lon
 */
interface MainView {
    /**
     * Add a {@link Listener} to the main view.
     *
     * @param l The listener to add.
     */
    void addListener(Listener l);

    /**
     * Event types dispatched by main view.
     *
     * @author Rinde van Lon
     */
    enum EventType {
        /**
         * Indicates that the view is shown.
         */
        SHOW
    }
}

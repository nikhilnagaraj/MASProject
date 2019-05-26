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
package ui.renderers;

import com.github.rinde.rinsim.event.Event;
import com.github.rinde.rinsim.event.Listener;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import core.model.DependencyProvider;
import core.model.ModelBuilder.AbstractModelBuilder;
import core.model.comm.CommDevice;
import core.model.comm.CommModel;
import core.model.comm.CommModel.CommModelEvent;
import core.model.comm.CommUser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import ui.renderers.CanvasRenderer.AbstractTypedCanvasRenderer;

import javax.annotation.CheckReturnValue;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.Verify.verify;

/**
 * A renderer for {@link CommModel}. Draws dots for a device, a circle is drawn
 * around it if it has a maximum range. It has several options which can be
 * configured via {@link #builder()}.
 *
 * @author Rinde van Lon
 */
public final class CommRenderer extends AbstractTypedCanvasRenderer<CommUser> {
    static final int OPAQUE = 255;
    static final int SEMI_TRANSPARENT = 50;
    static final double DOT_RADIUS = .05;

    final RenderHelper helper;
    final Set<ViewOptions> viewOptions;
    final RGB reliableColor;
    final RGB unreliableColor;
    private final Map<CommUser, DeviceUI> uiObjects;
    private final CommModel model;

    CommRenderer(Builder b, CommModel m) {
        model = m;
        viewOptions = b.viewOptions();
        reliableColor = b.reliableColor();
        unreliableColor = b.unreliableColor();
        helper = new RenderHelper();
        uiObjects = Collections
                .synchronizedMap(new LinkedHashMap<CommUser, DeviceUI>());
        for (final Entry<CommUser, CommDevice> entry : model.getUsersAndDevices()
                .entrySet()) {
            addUIObject(entry.getKey(), entry.getValue());
        }

        model.getEventAPI().addListener(new Listener() {
                                            @Override
                                            public void handleEvent(Event e) {
                                                verify(e instanceof CommModelEvent);
                                                final CommModelEvent event = (CommModelEvent) e;
                                                if (e.getEventType() == CommModel.EventTypes.ADD_COMM_USER) {
                                                    addUIObject(event.getUser(), event.getDevice());
                                                } else {
                                                    removeUIObject(event.getUser());
                                                }
                                            }
                                        }, CommModel.EventTypes.ADD_COMM_USER,
                CommModel.EventTypes.REMOVE_COMM_USER);
    }

    /**
     * @return A new {@link Builder} for creating a {@link CommRenderer}.
     */
    @CheckReturnValue
    public static Builder builder() {
        return Builder.create();
    }

    void addUIObject(CommUser u, CommDevice d) {
        uiObjects.put(u, new DeviceUI(u, d));
    }

    void removeUIObject(CommUser u) {
        uiObjects.remove(u);
    }

    @Override
    public void renderStatic(GC gc, ViewPort vp) {
    }

    @Override
    public void renderDynamic(GC gc, ViewPort vp, long time) {
        helper.adapt(gc, vp);

        synchronized (uiObjects) {
            for (final DeviceUI ui : uiObjects.values()) {
                ui.update(gc, vp, time);
            }
        }
    }

    @Override
    public boolean register(CommUser element) {
        uiObjects
                .put(element,
                        new DeviceUI(element, model.getUsersAndDevices().get(element)));
        return true;
    }

    @Override
    public boolean unregister(CommUser element) {
        return uiObjects.remove(element) != null;
    }

    enum ViewOptions {
        RELIABILITY_COLOR, MSG_COUNT, RELIABILITY_PERC, TO_STRING
    }

    /**
     * A builder for creating a {@link CommRenderer}.
     *
     * @author Rinde van Lon
     */

    public abstract static class Builder extends
            AbstractModelBuilder<CommRenderer, CommUser> {

        private static final long serialVersionUID = 3529315729865532162L;

        Builder() {
            setDependencies(CommModel.class);
        }

        static RGB defaultReliableColor() {
            return new RGB(0, OPAQUE, 0);
        }

        static RGB defaultUnreliableColor() {
            return new RGB(OPAQUE, 0, 0);
        }

        static RGB copy(RGB color) {
            return new RGB(color.red, color.green, color.blue);
        }

        static Builder create(Builder b, ViewOptions opt) {
            return create(b.reliableColor(), b.unreliableColor(),
                    ImmutableSet.<ViewOptions>builder()
                            .addAll(b.viewOptions())
                            .add(opt)
                            .build());
        }

        static Builder create(RGB rel, RGB unrel, ImmutableSet<ViewOptions> opts) {
            return new AutoValue_CommRenderer_Builder(rel, unrel, opts);
        }

        static Builder create() {
            return create(defaultReliableColor(), defaultUnreliableColor(),
                    ImmutableSet.<ViewOptions>of());
        }

        abstract RGB reliableColor();

        abstract RGB unreliableColor();

        abstract ImmutableSet<ViewOptions> viewOptions();

        /**
         * Draws the toString() representation of the {@link CommUser} near its
         * location.
         *
         * @return A new builder instance.
         */
        @CheckReturnValue
        public Builder withToString() {
            return create(this, ViewOptions.TO_STRING);
        }

        /**
         * Shows the reliability as a percentage for every {@link CommDevice} on the
         * map.
         *
         * @return A new builder instance.
         */
        @CheckReturnValue
        public Builder withReliabilityPercentage() {
            return create(this, ViewOptions.RELIABILITY_PERC);
        }

        /**
         * Shows the message counts for every {@link CommDevice} on the map. The
         * format for display is as follows: <code>XX(Y)</code> where
         * <code>XX</code> is the total number of received messages and
         * <code>Y</code> is the number of unread messages.
         *
         * @return A new builder instance.
         */
        @CheckReturnValue
        public Builder withMessageCount() {
            return create(this, ViewOptions.MSG_COUNT);
        }

        /**
         * Shows the reliability of all {@link CommDevice}s by drawing them in color
         * ranging from red (0% reliability) to green (100% reliability). For using
         * different colors see {@link #withReliabilityColors(RGB, RGB)}.
         *
         * @return A new builder instance.
         */
        @CheckReturnValue
        public Builder withReliabilityColors() {
            return create(this, ViewOptions.RELIABILITY_COLOR);
        }

        /**
         * Shows the reliability of all {@link CommDevice}s by drawing them in color
         * ranging from unreliable color (0% reliability) to reliable color (100%
         * reliability). For using the default colors red and green see
         * {@link #withReliabilityColors()}.
         *
         * @param unreliable The color that will be used as the negative extreme.
         * @param reliable   The color that will be used as the positive extreme.
         * @return A new builder instance.
         */
        @CheckReturnValue
        public Builder withReliabilityColors(RGB unreliable, RGB reliable) {
            return create(
                    copy(reliable),
                    copy(unreliable),
                    ImmutableSet.<ViewOptions>builder()
                            .addAll(viewOptions())
                            .add(ViewOptions.RELIABILITY_COLOR)
                            .build());
        }

        @Override
        public CommRenderer build(DependencyProvider dependencyProvider) {
            return new CommRenderer(this, dependencyProvider.get(CommModel.class));
        }
    }

    class DeviceUI {
        final CommUser user;
        final CommDevice device;
        Optional<Color> color;

        DeviceUI(CommUser u, CommDevice d) {
            user = u;
            device = d;
            color = Optional.absent();
        }

        void update(GC gc, ViewPort vp, long time) {
            final Optional<com.github.rinde.rinsim.geom.Point> maybe =
                    user.getPosition();
            if (!maybe.isPresent()) {
                return;
            }
            final com.github.rinde.rinsim.geom.Point userPos = maybe.get();
            if (!color.isPresent()
                    && viewOptions.contains(ViewOptions.RELIABILITY_COLOR)) {
                final RGB rgb = ColorUtil.interpolate(unreliableColor, reliableColor,
                        device.getReliability());
                color = Optional.of(new Color(gc.getDevice(), rgb));
            }

            if (device.getMaxRange().isPresent()) {
                helper.drawCircle(userPos, device.getMaxRange().get());
                if (color.isPresent()) {
                    gc.setBackground(color.get());
                } else {
                    helper.setBackgroundSysCol(SWT.COLOR_DARK_BLUE);
                }
                gc.setAlpha(SEMI_TRANSPARENT);
                helper.fillCircle(userPos, device.getMaxRange().get());
            }

            gc.setAlpha(OPAQUE);
            helper.fillCircle(userPos, DOT_RADIUS);

            if (viewOptions.contains(ViewOptions.TO_STRING)) {
                final Point p = gc.textExtent(user.toString());
                helper.drawString(user.toString(), userPos, true,
                        -p.x / 2, -p.y);
            }
            final StringBuilder sb = new StringBuilder();
            if (viewOptions.contains(ViewOptions.MSG_COUNT)) {
                sb.append(device.getReceivedCount())
                        .append('(')
                        .append(device.getUnreadCount())
                        .append(')');
            }
            if (viewOptions.contains(ViewOptions.RELIABILITY_PERC)) {
                sb.append(" rel:")
                        .append(String.format("%.2f", device.getReliability()));
            }
            if (sb.length() > 0) {
                final Point p = gc.textExtent(sb.toString());
                helper.drawString(sb.toString(), userPos, true,
                        -p.x / 2, 0);
            }
        }

        void dispose() {
            if (color.isPresent()) {
                color.get().dispose();
            }
        }
    }
}

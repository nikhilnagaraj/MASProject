//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.pdp.DefaultPDPModel;
import com.github.rinde.rinsim.core.model.pdp.Depot;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.ParcelDTO;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModelBuilders;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.event.Listener;
import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.geom.io.DotGraphIO;
import com.github.rinde.rinsim.geom.io.Filters;
import com.github.rinde.rinsim.ui.View;
import com.github.rinde.rinsim.ui.View.Builder;
import com.github.rinde.rinsim.ui.renderers.GraphRoadModelRenderer;
import com.github.rinde.rinsim.ui.renderers.RoadUserRenderer;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.math3.random.RandomGenerator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;

public final class TaxiExample {
    private static final int NUM_DEPOTS = 1;
    private static final int NUM_TAXIS = 20;
    private static final int NUM_CUSTOMERS = 30;
    private static final long SERVICE_DURATION = 60000L;
    private static final int TAXI_CAPACITY = 10;
    private static final int DEPOT_CAPACITY = 100;
    private static final int SPEED_UP = 4;
    private static final int MAX_CAPACITY = 3;
    private static final double NEW_CUSTOMER_PROB = 0.007D;
    private static final String MAP_FILE = "/data/maps/leuven-simple.dot";
    private static final Map<String, Graph<MultiAttributeData>> GRAPH_CACHE = Maps.newHashMap();
    private static final long TEST_STOP_TIME = 1200000L;
    private static final int TEST_SPEED_UP = 64;

    private TaxiExample() {
    }

    public static void main(@Nullable String[] args) {
        long endTime = args != null && args.length >= 1 ? Long.parseLong(args[0]) : 9223372036854775807L;
        String graphFile = args != null && args.length >= 2 ? args[1] : "/data/maps/leuven-simple.dot";
        run(false, endTime, graphFile, (Display)null, (Monitor)null, (Listener)null);
    }

    public static void run(boolean testing) {
        run(testing, 9223372036854775807L, "/data/maps/leuven-simple.dot", (Display)null, (Monitor)null, (Listener)null);
    }

    public static Simulator run(boolean testing, final long endTime, String graphFile, @Nullable Display display, @Nullable Monitor m, @Nullable Listener list) {
        Builder view = createGui(testing, display, m, list);
        final Simulator simulator = Simulator.builder().addModel(RoadModelBuilders.staticGraph(loadGraph(graphFile))).addModel(DefaultPDPModel.builder()).addModel(view).build();
        final RandomGenerator rng = simulator.getRandomGenerator();
        final RoadModel roadModel = (RoadModel)simulator.getModelProvider().getModel(RoadModel.class);

        int i;
        for(i = 0; i < 1; ++i) {
            simulator.register(new TaxiExample.TaxiBase(roadModel.getRandomPosition(rng), 100.0D));
        }

        for(i = 0; i < 20; ++i) {
            simulator.register(new Taxi(roadModel.getRandomPosition(rng), 10));
        }

        for(i = 0; i < 30; ++i) {
            simulator.register(new TaxiExample.Customer(Parcel.builder(roadModel.getRandomPosition(rng), roadModel.getRandomPosition(rng)).serviceDuration(60000L).neededCapacity((double)(1 + rng.nextInt(3))).buildDTO()));
        }

        simulator.addTickListener(new TickListener() {
            public void tick(TimeLapse time) {
                if (time.getStartTime() > endTime) {
                    simulator.stop();
                } else if (rng.nextDouble() < 0.007D) {
                    simulator.register(new TaxiExample.Customer(Parcel.builder(roadModel.getRandomPosition(rng), roadModel.getRandomPosition(rng)).serviceDuration(60000L).neededCapacity((double)(1 + rng.nextInt(3))).buildDTO()));
                }

            }

            public void afterTick(TimeLapse timeLapse) {
            }
        });
        simulator.start();
        return simulator;
    }

    static Builder createGui(boolean testing, @Nullable Display display, @Nullable Monitor m, @Nullable Listener list) {
        Builder view = View.builder().with(GraphRoadModelRenderer.builder()).with(RoadUserRenderer.builder().withImageAssociation(TaxiExample.TaxiBase.class, "/graphics/perspective/tall-building-64.png").withImageAssociation(Taxi.class, "/graphics/flat/taxi-32.png").withImageAssociation(TaxiExample.Customer.class, "/graphics/flat/person-red-32.png")).with(TaxiRenderer.builder(TaxiRenderer.Language.ENGLISH)).withTitleAppendix("Taxi example");
        if (testing) {
            view = view.withAutoClose().withAutoPlay().withSimulatorEndTime(1200000L).withSpeedUp(64);
        } else if (m != null && list != null && display != null) {
            view = view.withMonitor(m).withSpeedUp(4).withResolution(m.getClientArea().width, m.getClientArea().height).withDisplay(display).withCallback(list).withAsync().withAutoPlay().withAutoClose();
        }

        return view;
    }

    static Graph<MultiAttributeData> loadGraph(String name) {
        try {
            if (GRAPH_CACHE.containsKey(name)) {
                return (Graph)GRAPH_CACHE.get(name);
            } else {
                Graph<MultiAttributeData> g = DotGraphIO.getMultiAttributeGraphIO(Filters.selfCycleFilter()).read(TaxiExample.class.getResourceAsStream(name));
                GRAPH_CACHE.put(name, g);
                return g;
            }
        } catch (FileNotFoundException var2) {
            throw new IllegalStateException(var2);
        } catch (IOException var3) {
            throw new IllegalStateException(var3);
        }
    }

    static class TaxiBase extends Depot {
        TaxiBase(Point position, double capacity) {
            super(position);
            this.setCapacity(capacity);
        }

        public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
        }
    }

    static class Customer extends Parcel {
        Customer(ParcelDTO dto) {
            super(dto);
        }

        public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
        }
    }
}

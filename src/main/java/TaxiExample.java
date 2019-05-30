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

import com.github.rinde.rinsim.event.Listener;
import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.geom.io.DotGraphIO;
import com.github.rinde.rinsim.geom.io.Filters;
import com.opencsv.CSVWriter;
import core.Simulator;
import core.model.pdp.*;
import core.model.road.RoadModel;
import core.model.road.RoadModelBuilders;
import core.model.time.TickListener;
import core.model.time.TimeLapse;
import org.apache.commons.math3.random.RandomGenerator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import ui.View;
import ui.renderers.GraphRoadModelRenderer;
import ui.renderers.RoadUserRenderer;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Example showing a fleet of taxis that have to pickup and transport customers
 * around the city of Leuven.
 * <p>
 * If this class is run on MacOS it might be necessary to use
 * -XstartOnFirstThread as a VM argument.
 *
 * @author Rinde van Lon
 */
public final class TaxiExample {

    private static final int NUM_DEPOTS = 1;
    private static final int NUM_TAXIS = 20;
    private static final int NUM_CUSTOMERS = 10;
    private static final int NUM_CHARGING_STATIONS = 5;
    private static final int NUM_CHARGING_LOCATIONS = 20;
    private static final int NUM_WAITING_SPOTS = 3;

    // use the following variables to determine where the customers are allowed to spawn
    // exp: BIASED_CUSTOMERS_N = true  ---> customers are allowed to spawn and choose their destination on the northern part of the map (< POINT_OF_REFERENCE_Y)
    private static final boolean BIASED_CUSTOMERS_N = false;
    private static final boolean BIASED_CUSTOMERS_S = false;
    private static final boolean BIASED_CUSTOMERS_W = true;
    private static final boolean BIASED_CUSTOMERS_E = false;

    static double POINT_OF_REFERENCE_X = 3290000.0;
    static double POINT_OF_REFERENCE_Y = 2.571E7;

    // time in ms
    private static final long SERVICE_DURATION = 60000;
    private static final int TAXI_CAPACITY = 10;
    private static final int DEPOT_CAPACITY = 100;
    private static final int TICKS_AT_LOCATION = 1000;
    private static final int TOTAL_BATTERY_CAPACITY = 20000;
    private static final int RATE_OF_CHARGE = 100;
    private static final int SPEED_UP = 4;
    private static final int MAX_CAPACITY = 3;
    private static final double NEW_CUSTOMER_PROB = .005;

    private static final String MAP_FILE = "/data/maps/leuven-simple.dot";
    private static final Map<String, Graph<MultiAttributeData>> GRAPH_CACHE =
            newHashMap();

    private static final long TEST_STOP_TIME = 20 * 60 * 1000;
    private static final int TEST_SPEED_UP = 64;


    public static String experimentID;

    // statistical evaluation
    static String STATISTICS_PATH = "statistics/";
    static File statisticsCSV;
    static FileWriter outputfile;
    static CSVWriter writer;
    static int totalNumOfCustomersShowingUp;
    static int totalNumOfCustomersPickedUp;
    static int totalTimeOfBatteryChargedUp;
    static int totalTimeOfChargingAgentMovement;
    static int totalTimeCustomerSpendsWaiting;

    private TaxiExample() {
    }

    /**
     * Starts the {@link TaxiExample}.
     *
     * @param args The first option may optionally indicate the end time of the
     *             simulation.
     */
    public static void main(@Nullable String[] args) throws IOException {
        initStatistics(STATISTICS_PATH);

        //Setup Logging directory
        try {
            long countLogs = Files.find(
                    Paths.get("logs"),
                    1,  // how deep do we want to descend
                    (path, attributes) -> attributes.isDirectory()
            ).count() - 1; // '-1' because '/logs' is also counted in
            experimentID = String.valueOf(countLogs + 1);
            new File(String.format("logs/" + experimentID)).mkdir();
            new File(String.format("chlogs/" + experimentID)).mkdir();
        } catch (IOException e) {
            throw new IOException("Folder does not exist.");
        }

        //Setup and run example
        final long endTime = args != null && args.length >= 1 ? Long
                .parseLong(args[0]) : Long.MAX_VALUE;

        final String graphFile = args != null && args.length >= 2 ? args[1]
                : MAP_FILE;
        run(false, endTime, graphFile, null /* new Display() */, null, null);

        try {
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Run the example.
     *
     * @param testing If <code>true</code> enables the test mode.
     */
    public static void run(boolean testing) {
        run(testing, Long.MAX_VALUE, MAP_FILE, null, null, null);
    }

    /**
     * Starts the example.
     *
     * @param testing   Indicates whether the method should run in testing mode.
     * @param endTime   The time at which simulation should stop.
     * @param graphFile The graph that should be loaded.
     * @param display   The display that should be used to show the ui on.
     * @param m         The monitor that should be used to show the ui on.
     * @param list      A listener that will receive callbacks from the ui.
     * @return The simulator instance.
     */
    public static Simulator run(boolean testing, final long endTime,
                                String graphFile,
                                @Nullable Display display, @Nullable Monitor m, @Nullable Listener list) {

        final View.Builder view = createGui(testing, display, m, list);

        // use map of leuven
        final Simulator simulator = Simulator.builder()
                .addModel(RoadModelBuilders.staticGraph(loadGraph(graphFile)))
                .addModel(DefaultPDPModel.builder())
                .addModel(view)
                .build();
        final RandomGenerator rng = Simulator.getRandomGenerator();

        final RoadModel roadModel = simulator.getModelProvider().getModel(
                RoadModel.class);
        // add depots, taxis and parcels to simulator
        for (int i = 0; i < NUM_DEPOTS; i++) {
            simulator.register(new TaxiBase(roadModel.getRandomPosition(rng),
                    DEPOT_CAPACITY));
        }
        for (int i = 0; i < NUM_CHARGING_LOCATIONS; i++) {
            UUID ID = UUID.randomUUID();
            PheromoneInfrastructure pheromoneInfrastructure = new PheromoneInfrastructure(ID);
            simulator.register(pheromoneInfrastructure);
            simulator.register(new Candidate(pheromoneInfrastructure,
                    roadModel.getRandomPosition(rng), NUM_WAITING_SPOTS, ID));
        }

        Set<Candidate> chargingLocationSet = roadModel.getObjectsOfType(Candidate.class);
        for (Candidate candidate : chargingLocationSet) {
            candidate.setOtherCandidates(chargingLocationSet);
        }

        for (int i = 0; i < NUM_TAXIS; i++) {
            AgentBattery newBattery = new AgentBattery(TOTAL_BATTERY_CAPACITY, RATE_OF_CHARGE);
            simulator.register(new Taxi(roadModel.getRandomPosition(rng),
                    TAXI_CAPACITY, newBattery, UUID.randomUUID()));
        }

        ArrayList<Candidate> chargingLocations = new ArrayList<Candidate>(chargingLocationSet);

        for (int i = 0; i < NUM_CHARGING_STATIONS; i++) {
            int randomSelector = rng.nextInt(chargingLocations.size());
            simulator.register(new ChargingAgent(chargingLocations.get(randomSelector).getPosition(),
                    TICKS_AT_LOCATION, UUID.randomUUID()));
            chargingLocations.remove(randomSelector);
        }

        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            Point rnd_spawn = generateBiasedPosition(roadModel, rng, BIASED_CUSTOMERS_N, BIASED_CUSTOMERS_S, BIASED_CUSTOMERS_W, BIASED_CUSTOMERS_E);
            Point rnd_dest = generateBiasedPosition(roadModel, rng, BIASED_CUSTOMERS_N, BIASED_CUSTOMERS_S, BIASED_CUSTOMERS_W, BIASED_CUSTOMERS_E);
            simulator.register(new Customer(
                    Parcel.builder(rnd_spawn, rnd_dest)
                            .serviceDuration(SERVICE_DURATION)
                            .neededCapacity(1 + rng.nextInt(MAX_CAPACITY))
                            .buildDTO()));
        }

        simulator.addTickListener(new TickListener() {
            @Override
            public void tick(TimeLapse time) {
                if (time.getStartTime() > endTime) {
                    simulator.stop();
                } else if (rng.nextDouble() < NEW_CUSTOMER_PROB) {
                        Point rnd_spawn = generateBiasedPosition(roadModel, rng, BIASED_CUSTOMERS_N, BIASED_CUSTOMERS_S, BIASED_CUSTOMERS_W, BIASED_CUSTOMERS_E);
                        Point rnd_dest = generateBiasedPosition(roadModel, rng, BIASED_CUSTOMERS_N, BIASED_CUSTOMERS_S, BIASED_CUSTOMERS_W, BIASED_CUSTOMERS_E);
                        simulator.register(new Customer(
                                Parcel
                                        .builder(rnd_spawn, rnd_dest)
                                        .serviceDuration(SERVICE_DURATION)
                                        .neededCapacity(1 + rng.nextInt(MAX_CAPACITY))
                                        .buildDTO()));
                        totalNumOfCustomersShowingUp++;
                }
            }

            /***
             * save statistics after tick
             * @param timeLapse The time lapse that is handed to this object.
             */
            @Override
            public void afterTick(TimeLapse timeLapse) {
                for(Taxi taxi : roadModel.getObjectsOfType(Taxi.class)) {
                    if(taxi.pickedUpCustomerThisTurn)
                        totalNumOfCustomersPickedUp++;
                }
                for(ChargingAgent chargingAgent : roadModel.getObjectsOfType(ChargingAgent.class)){
                    if(chargingAgent.isActiveUsage()){
                        totalTimeOfBatteryChargedUp++;
                    }
                    if(chargingAgent.isMoving()){
                        totalTimeOfChargingAgentMovement++;
                    }
                }
                String[] data = {
                        Long.toString(timeLapse.getTime()),
                        Integer.toString(totalNumOfCustomersShowingUp),
                        Integer.toString(totalNumOfCustomersPickedUp),
                        Integer.toString(totalTimeOfBatteryChargedUp),
                        Integer.toString(totalTimeOfChargingAgentMovement),
                        Integer.toString(Taxi.numOfDeadBatteries),
                        Double.toString(Taxi.distanceTravelledToDepot),
                        Double.toString(Taxi.distanceTravelledToChargingAgent),
                        Double.toString(Taxi.distanceTravelledToCustomer)};
                writer.writeNext(data);
            }
        });
        simulator.start();
        return simulator;
    }

    /**
     * Use this method to generate a random point that fulfills the constraints
     * towards POINT_OF_REFERENCE_X and POINT_OF_REFERENCE_Y
     * @param roadModel the road model on which the biased random point should be generated
     * @param rng the random number generator used to generate a biased point
     * @param north if true, points north of POINT_OF_REFERENCE_Y will be generated
     * @param south if true, points south of POINT_OF_REFERENCE_Y will be generated
     * @param west if true, points west of POINT_OF_REFERENCE_X will be generated
     * @param east if true, points east of POINT_OF_REFERENCE_X will be generated
     * @return point that adheres given constraints
     */
    static Point generateBiasedPosition(RoadModel roadModel,
                                        RandomGenerator rng,
                                        boolean north,
                                        boolean south,
                                        boolean west,
                                        boolean east){
        Point point = roadModel.getRandomPosition(rng);
        if(north && point.y >= POINT_OF_REFERENCE_Y)
            point = generateBiasedPosition(roadModel, rng, true, south, west, east);
        if(south && point.y < POINT_OF_REFERENCE_Y)
            point = generateBiasedPosition(roadModel, rng, north, true, west, east);
        if(west && point.x > POINT_OF_REFERENCE_X)
            point = generateBiasedPosition(roadModel, rng, north, south, true, east);
        if(east && point.x <= POINT_OF_REFERENCE_X)
            point = generateBiasedPosition(roadModel, rng, north, south, west, true);
        return point;
    }

    static View.Builder createGui(
            boolean testing,
            @Nullable Display display,
            @Nullable Monitor m,
            @Nullable Listener list) {

        View.Builder view = View.builder()
                .with(GraphRoadModelRenderer.builder())
                .with(RoadUserRenderer.builder()
                        .withImageAssociation(
                                TaxiBase.class, "/graphics/perspective/tall-building-64.png")
                        .withImageAssociation(
                                Candidate.class, "/graphics/perspective/flag.png")
                        .withImageAssociation(
                                Taxi.class, "/graphics/flat/taxi-32.png")
                        .withImageAssociation(
                                ChargingAgent.class, "/graphics/flat/warehouse-32.png")
                        .withImageAssociation(
                                Customer.class, "/graphics/flat/person-blue-32.png"))
                .with(TaxiRenderer.builder(TaxiRenderer.Language.ENGLISH))
                .withTitleAppendix("Taxi example");

        if (testing) {
            view = view.withAutoClose()
                    .withAutoPlay()
                    .withSimulatorEndTime(TEST_STOP_TIME)
                    .withSpeedUp(TEST_SPEED_UP);
        } else if (m != null && list != null && display != null) {
            view = view.withMonitor(m)
                    .withSpeedUp(SPEED_UP)
                    .withResolution(m.getClientArea().width, m.getClientArea().height)
                    .withDisplay(display)
                    .withCallback(list)
                    .withAsync()
                    .withAutoPlay()
                    .withAutoClose();
        }
        return view;
    }

    // load the graph file
    static Graph<MultiAttributeData> loadGraph(String name) {
        try {
            if (GRAPH_CACHE.containsKey(name)) {
                return GRAPH_CACHE.get(name);
            }
            final Graph<MultiAttributeData> g = DotGraphIO
                    .getMultiAttributeGraphIO(
                            Filters.selfCycleFilter())
                    .read(
                            TaxiExample.class.getResourceAsStream(name));

            GRAPH_CACHE.put(name, g);
            return g;
        } catch (final FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * A customer with very permissive time windows.
     */
    static class Customer extends Parcel {
        Customer(ParcelDTO dto) {
            super(dto);
        }

        @Override
        public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
        }
    }

    // currently has no function
    static class TaxiBase extends Depot {
        TaxiBase(Point position, double capacity) {
            super(position);
            setCapacity(capacity);
        }

        @Override
        public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
        }
    }

    private static void initStatistics(String filePath){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMHHmmss");
        LocalDateTime now = LocalDateTime.now();
        String filename_statistics = filePath + "stats_" + dtf.format(now) + ".csv";
        String filename_metrics = filePath + "metrics_" + dtf.format(now) + ".csv";
        System.out.println("Save statistics for this run in " + filename_statistics);
        System.out.println("Save metrics for this run in " + filename_metrics);
        statisticsCSV = new File(filename_statistics);
        File metricsCSV = new File(filename_metrics);
        try {
            outputfile = new FileWriter(statisticsCSV);
            writer = new CSVWriter(outputfile);
            String[] header =
                    { "Tick",
                            "numOfCustShowUps",
                            "numOfCustPickUps",
                            "timeForCharging",
                            "timeBatteryStationMoving",
                            "numOfDeadBatteries",
                            "distanceTravelledToDepot",
                            "distanceTravelledToChargingAgent",
                            "distanceTravelledToCustomer"};
            writer.writeNext(header);
            FileWriter outputfile_metrics = new FileWriter(metricsCSV);
            CSVWriter writer_metrics = new CSVWriter(outputfile_metrics);
            String[] header_metrics =
                    {
                            "NUM_DEPOTS",
                            "NUM_TAXIS",
                            "NUM_CUSTOMERS",
                            "NUM_CHARGING_STATIONS",
                            "NUM_CHARGING_LOCATIONS",
                            "NUM_WAITING_SPOTS",
                            "SERVICE_DURATION",
                            "TAXI_CAPACITY",
                            "DEPOT_CAPACITY",
                            "TICKS_AT_LOCATION",
                            "TOTAL_BATTERY_CAPACITY",
                            "RATE_OF_CHARGE",
                            "SPEED_UP",
                            "MAX_CAPACITY",
                            "NEW_CUSTOMER_PROB"
                    };
            String[] metrics =
                    {
                            Integer.toString(NUM_DEPOTS),
                            Integer.toString(NUM_TAXIS),
                            Integer.toString(NUM_CUSTOMERS),
                            Integer.toString(NUM_CHARGING_STATIONS),
                            Integer.toString(NUM_CHARGING_LOCATIONS),
                            Integer.toString(NUM_WAITING_SPOTS),
                            Long.toString(SERVICE_DURATION),
                            Integer.toString(TAXI_CAPACITY),
                            Integer.toString(DEPOT_CAPACITY),
                            Integer.toString(TICKS_AT_LOCATION),
                            Integer.toString(TOTAL_BATTERY_CAPACITY),
                            Integer.toString(RATE_OF_CHARGE),
                            Integer.toString(SPEED_UP),
                            Integer.toString(MAX_CAPACITY),
                            Double.toString(NEW_CUSTOMER_PROB)
                    };
            writer_metrics.writeNext(header_metrics);
            writer_metrics.writeNext(metrics);
            writer_metrics.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        totalNumOfCustomersShowingUp = NUM_CUSTOMERS;
    }
}

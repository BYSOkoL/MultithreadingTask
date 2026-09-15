package com.task.port;

import com.task.port.entity.Container;
import com.task.port.entity.Ship;
import com.task.port.manager.BerthManager;
import com.task.port.manager.PortWarehouse;
import com.task.port.reader.PortConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Port {
    private static final Logger logger = LogManager.getLogger(Port.class);

    private final PortConfigReader configReader;
    private final BerthManager berthManager;
    private final PortWarehouse warehouse;
    private ExecutorService executorService;

    public Port(PortConfigReader configReader) {
        this.configReader = configReader;
        this.berthManager = BerthManager.getInstance();
        this.warehouse = PortWarehouse.getInstance();
    }

    public void run() {
        logger.info("Port application started");
        try {
            Map<String, Integer> config = configReader.readConfig();
            initializePort(config);
            initializeWarehouse(config);
            createAndStartShips(config);
            waitForCompletion();
            logFinalStatistics();
        } catch (Exception e) {
            logger.error("Port application terminated with error: {}", e.getMessage(), e);
        }
        logger.info("Port application finished");
    }

    private void initializePort(Map<String, Integer> config) {
        int numBerths = config.getOrDefault("berths", 3);
        berthManager.initialize(numBerths);
        logger.info("Port initialized with {} berths", numBerths);
    }

    private void initializeWarehouse(Map<String, Integer> config) {
        int warehouseCapacity = config.getOrDefault("warehouse_capacity", 100);
        int initialContainers = config.getOrDefault("initial_warehouse_containers", 30);
        List<Container> initialContainerList = new ArrayList<>();
        for (int i = 1; i <= initialContainers; i++) {
            String containerId = "WH-CONT-" + i;
            initialContainerList.add(new Container(containerId));
        }
        warehouse.initialize(warehouseCapacity, initialContainerList);
        logger.info("Warehouse initialized with capacity {} and {} initial containers",
                warehouseCapacity, initialContainers);
    }

    private void createAndStartShips(Map<String, Integer> config) {
        int numShips = config.getOrDefault("ships", 5);
        int minUnload = config.getOrDefault("min_containers_to_unload", 3);
        int maxUnload = config.getOrDefault("max_containers_to_unload", 10);
        int minLoad = config.getOrDefault("min_containers_to_load", 3);
        int maxLoad = config.getOrDefault("max_containers_to_load", 10);
        int shipCapacityMin = config.getOrDefault("ship_capacity_min", 15);
        int shipCapacityMax = config.getOrDefault("ship_capacity_max", 25);

        executorService = Executors.newFixedThreadPool(numShips);

        for (int i = 1; i <= numShips; i++) {
            String shipId = "SHIP-" + i;
            String shipName = "Vessel-" + i;
            // ✅ Используем ThreadLocalRandom вместо Math.random() — быстрее и безопаснее в многопоточке
            int capacity = ThreadLocalRandom.current().nextInt(shipCapacityMin, shipCapacityMax + 1);
            List<Container> toUnload = generateContainers(shipId, "unload", minUnload, maxUnload);
            int toLoadCount = ThreadLocalRandom.current().nextInt(minLoad, maxLoad + 1);

            Ship ship = new Ship(shipId, shipName, capacity, toUnload, toLoadCount);
            logger.info("Created ship: {} ({}), capacity: {}, to unload: {}, to load: {}",
                    shipName, shipId, capacity, toUnload.size(), toLoadCount);
            executorService.submit(ship);
        }
    }

    private List<Container> generateContainers(String shipId, String type, int min, int max) {
        List<Container> containers = new ArrayList<>();
        int count = ThreadLocalRandom.current().nextInt(min, max + 1); // ✅ ThreadLocalRandom
        for (int i = 1; i <= count; i++) {
            String containerId = shipId + "-" + type + "-" + i;
            containers.add(new Container(containerId));
        }
        return containers;
    }

    private void waitForCompletion() {
        logger.info("Waiting for all ships to be serviced...");
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(30, TimeUnit.MINUTES)) {
                logger.warn("Some ships did not complete in time");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Port interrupted while waiting for ships", e);
        }
    }

    private void logFinalStatistics() {
        logger.info("=== Final Port Statistics ===");
        logger.info("Warehouse final occupancy: {}/{}",
                warehouse.getCurrentContainers(), warehouse.getCapacity());
        logger.info("Berths available: {}/{}",
                berthManager.getAvailableBerths(), berthManager.getTotalBerths());
        logger.info("================================");
    }
}
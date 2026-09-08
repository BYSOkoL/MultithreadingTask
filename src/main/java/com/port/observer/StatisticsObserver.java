package com.port.observer;

import com.port.manager.BerthManager;
import com.port.manager.PortWarehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsObserver implements PortObserver {

    private static final Logger logger = LogManager.getLogger(StatisticsObserver.class);
    private final BerthManager berthManager;
    private final PortWarehouse warehouse;
    private int totalShipsServiced;
    private int totalContainersLoaded;
    private int totalContainersUnloaded;

    public StatisticsObserver(BerthManager berthManager, PortWarehouse warehouse) {
        this.berthManager = berthManager;
        this.warehouse = warehouse;
        this.totalShipsServiced = 0;
        this.totalContainersLoaded = 0;
        this.totalContainersUnloaded = 0;
    }

    @Override
    public void update(String event) {
        if (event.contains("SERVICED")) {
            totalShipsServiced++;
        } else if (event.contains("Loaded")) {
            totalContainersLoaded++;
        } else if (event.contains("Unloaded")) {
            totalContainersUnloaded++;
        }
        logStatistics();
    }

    private void logStatistics() {
        logger.info("=== Port Statistics ===");
        logger.info("Ships serviced: {}", totalShipsServiced);
        logger.info("Containers loaded: {}", totalContainersLoaded);
        logger.info("Containers unloaded: {}", totalContainersUnloaded);
        logger.info("Warehouse occupancy: {}/{}", warehouse.getCurrentContainers(), warehouse.getCapacity());
        logger.info("Berths occupied: {}/{}", berthManager.getOccupiedBerths(), berthManager.getTotalBerths());
        logger.info("========================");
    }

    public int getTotalShipsServiced() {
        return totalShipsServiced;
    }

    public int getTotalContainersLoaded() {
        return totalContainersLoaded;
    }

    public int getTotalContainersUnloaded() {
        return totalContainersUnloaded;
    }
}

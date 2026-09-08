package com.port.state;

import com.port.entity.Container;
import com.port.entity.Ship;
import com.port.manager.BerthManager;
import com.port.manager.PortWarehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class UnloadingState implements ShipState {

    private static final Logger logger = LogManager.getLogger(UnloadingState.class);
    private final Ship ship;
    private final ShipStateFactory stateFactory;
    private final PortWarehouse warehouse;
    private final String berthId;

    public UnloadingState(Ship ship, ShipStateFactory stateFactory,
                          PortWarehouse warehouse, String berthId) {
        this.ship = ship;
        this.stateFactory = stateFactory;
        this.warehouse = warehouse;
        this.berthId = berthId;
    }

    @Override
    public void handle() {
        logger.info("Ship {} ({}) is unloading containers at {}",
                ship.getName(), ship.getId(), berthId);
        try {
            while (ship.hasContainersToUnload()) {
                Container container = ship.getContainersToUnload().get(0);
                boolean added = warehouse.addContainer(container);
                if (added) {
                    ship.removeContainerToUnload(container);
                    ship.addUnloadedContainer(container);
                    logger.info("Unloaded container {} from ship {} to warehouse",
                            container.getId(), ship.getName());
                    TimeUnit.MILLISECONDS.sleep(300);
                } else {
                    logger.warn("Warehouse full, waiting to unload container {} from ship {}",
                            container.getId(), ship.getName());
                    TimeUnit.SECONDS.sleep(1);
                }
            }

            if (ship.getContainersToLoadCount() > ship.getLoadedCount()) {
                ship.setState(stateFactory.createLoadingState(warehouse, berthId));
            } else {
                ship.setState(stateFactory.createDepartingState(
                        BerthManager.getInstance(), berthId));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Ship {} ({}) interrupted while unloading", ship.getName(), ship.getId());
        }
    }

    @Override
    public String getStateName() {
        return "UNLOADING";
    }
}

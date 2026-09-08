package com.port.state;

import com.port.entity.Container;
import com.port.entity.Ship;
import com.port.manager.BerthManager;
import com.port.manager.PortWarehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class LoadingState implements ShipState {

    private static final Logger logger = LogManager.getLogger(LoadingState.class);
    private final Ship ship;
    private final ShipStateFactory stateFactory;
    private final PortWarehouse warehouse;
    private final String berthId;

    public LoadingState(Ship ship, ShipStateFactory stateFactory,
                        PortWarehouse warehouse, String berthId) {
        this.ship = ship;
        this.stateFactory = stateFactory;
        this.warehouse = warehouse;
        this.berthId = berthId;
    }

    @Override
    public void handle() {
        logger.info("Ship {} ({}) is loading containers at {}",
                ship.getName(), ship.getId(), berthId);
        try {
            int targetLoad = ship.getContainersToLoadCount();
            while (ship.getLoadedCount() < targetLoad && ship.getAvailableSpace() > 0) {
                Container container = warehouse.getContainerForShip(ship);
                if (container != null) {
                    ship.addLoadedContainer(container);
                    logger.info("Loaded container {} from warehouse to ship {}",
                            container.getId(), ship.getName());
                    TimeUnit.MILLISECONDS.sleep(300);
                } else {
                    logger.warn("No container available in warehouse for ship {}", ship.getName());
                    TimeUnit.SECONDS.sleep(1);
                }
            }
            ship.setState(stateFactory.createDepartingState(
                    BerthManager.getInstance(), berthId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Ship {} ({}) interrupted while loading", ship.getName(), ship.getId());
        }
    }

    @Override
    public String getStateName() {
        return "LOADING";
    }
}

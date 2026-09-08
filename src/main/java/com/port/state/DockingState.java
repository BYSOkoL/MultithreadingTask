package com.port.state;

import com.port.entity.Ship;
import com.port.manager.BerthManager;
import com.port.manager.PortWarehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class DockingState implements ShipState {

    private static final Logger logger = LogManager.getLogger(DockingState.class);
    private final Ship ship;
    private final ShipStateFactory stateFactory;
    private final BerthManager berthManager;
    private final String berthId;
    private final PortWarehouse warehouse;

    public DockingState(Ship ship, ShipStateFactory stateFactory,
                        BerthManager berthManager, String berthId) {
        this.ship = ship;
        this.stateFactory = stateFactory;
        this.berthManager = berthManager;
        this.berthId = berthId;
        this.warehouse = PortWarehouse.getInstance();
    }

    @Override
    public void handle() {
        logger.info("Ship {} ({}) is docking at {}", ship.getName(), ship.getId(), berthId);
        try {
            TimeUnit.SECONDS.sleep(1);

            if (ship.hasContainersToUnload()) {
                ship.setState(stateFactory.createUnloadingState(warehouse, berthId));
            } else if (ship.getContainersToLoadCount() > 0) {
                ship.setState(stateFactory.createLoadingState(warehouse, berthId));
            } else {
                ship.setState(stateFactory.createDepartingState(berthManager, berthId));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Ship {} ({}) interrupted while docking", ship.getName(), ship.getId());
        }
    }

    @Override
    public String getStateName() {
        return "DOCKING";
    }
}

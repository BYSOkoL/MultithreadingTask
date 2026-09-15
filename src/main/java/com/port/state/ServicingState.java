package com.port.state;

import com.port.entity.Container;
import com.port.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class ServicingState implements ShipState {

    private static final Logger logger = LogManager.getLogger(ServicingState.class);
    private final Ship ship;

    public ServicingState(Ship ship) {
        this.ship = ship;
    }

    @Override
    public void handle() throws InterruptedException {
        unloadCargo();
        loadCargo();
        ship.getBerths().free();
        logger.info("{} finished servicing and left the berth", ship.getId());
        ship.setState(new ServicedState(ship));
    }

    private void unloadCargo() throws InterruptedException {
        while (ship.hasCargo()) {
            Container container = ship.takeNextCargo();
            while (!ship.getWarehouse().store(container)) {
                logger.warn("{} waits: warehouse is full", ship.getId());
                TimeUnit.MILLISECONDS.sleep(300);
            }
            logger.info("{} unloaded {} to warehouse", ship.getId(), container.getId());
            TimeUnit.MILLISECONDS.sleep(200);
        }
    }

    private void loadCargo() throws InterruptedException {
        while (ship.getLoadedCount() < ship.getContainersToLoad()
                && ship.getLoadedCount() < ship.getCapacity()) {
            Container container = ship.getWarehouse().take();
            if (container == null) {
                logger.warn("{} stops loading: warehouse is empty", ship.getId());
                break;
            }
            ship.addLoaded(container);
            logger.info("{} loaded {} from warehouse", ship.getId(), container.getId());
            TimeUnit.MILLISECONDS.sleep(200);
        }
    }
}
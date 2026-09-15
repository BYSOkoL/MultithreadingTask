package com.port.state;

import com.port.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServicedState implements ShipState {

    private static final Logger logger = LogManager.getLogger(ServicedState.class);
    private final Ship ship;

    public ServicedState(Ship ship) {
        this.ship = ship;
    }

    @Override
    public void handle() {
        logger.info("{} is serviced: {} containers on board", ship.getId(), ship.getLoadedCount());
        ship.setServiced(true);
    }
}
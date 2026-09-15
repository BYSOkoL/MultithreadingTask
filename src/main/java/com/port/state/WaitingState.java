package com.port.state;

import com.port.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WaitingState implements ShipState {

    private static final Logger logger = LogManager.getLogger(WaitingState.class);
    private final Ship ship;

    public WaitingState(Ship ship) {
        this.ship = ship;
    }

    @Override
    public void handle() throws InterruptedException {
        logger.info("{} waits for a free berth", ship.getId());
        ship.getBerths().occupy();
        logger.info("{} moored at a berth", ship.getId());
        ship.setState(new ServicingState(ship));
    }
}
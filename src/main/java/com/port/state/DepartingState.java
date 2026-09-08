package com.port.state;

import com.port.entity.Ship;
import com.port.manager.BerthManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class DepartingState implements ShipState {

    private static final Logger logger = LogManager.getLogger(DepartingState.class);
    private final Ship ship;
    private final ShipStateFactory stateFactory;
    private final BerthManager berthManager;
    private final String berthId;

    public DepartingState(Ship ship, ShipStateFactory stateFactory,
                          BerthManager berthManager, String berthId) {
        this.ship = ship;
        this.stateFactory = stateFactory;
        this.berthManager = berthManager;
        this.berthId = berthId;
    }

    @Override
    public void handle() {
        logger.info("Ship {} ({}) is departing from {}", ship.getName(), ship.getId(), berthId);
        try {
            TimeUnit.SECONDS.sleep(1);
            berthManager.releaseBerth(ship, berthId);
            ship.setState(stateFactory.createServicedState());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Ship {} ({}) interrupted while departing", ship.getName(), ship.getId());
        }
    }

    @Override
    public String getStateName() {
        return "DEPARTING";
    }
}

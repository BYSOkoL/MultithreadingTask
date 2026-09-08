package com.port.state;

import com.port.entity.Ship;
import com.port.manager.BerthManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WaitingState implements ShipState {

    private static final Logger logger = LogManager.getLogger(WaitingState.class);
    private final Ship ship;
    private final ShipStateFactory stateFactory;
    private final BerthManager berthManager;

    public WaitingState(Ship ship, ShipStateFactory stateFactory) {
        this.ship = ship;
        this.stateFactory = stateFactory;
        this.berthManager = BerthManager.getInstance();
    }

    @Override
    public void handle() {
        logger.info("Ship {} ({}) is waiting for berth", ship.getName(), ship.getId());
        try {
            String berthId = berthManager.acquireBerth(ship);
            ship.setState(stateFactory.createDockingState(berthManager, berthId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Ship {} ({}) interrupted while waiting", ship.getName(), ship.getId());
        }
    }

    @Override
    public String getStateName() {
        return "WAITING";
    }
}

package com.task.port.state;

import com.task.port.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServicedState implements ShipState {

    private static final Logger logger = LogManager.getLogger(ServicedState.class);
    private final Ship ship;
    private final ShipStateFactory stateFactory;

    public ServicedState(Ship ship, ShipStateFactory stateFactory) {
        this.ship = ship;
        this.stateFactory = stateFactory;
    }

    @Override
    public void handle() {
        logger.info("Ship {} ({}) has been fully serviced", ship.getName(), ship.getId());
        ship.setServiced(true);
    }

    @Override
    public String getStateName() {
        return "SERVICED";
    }
}

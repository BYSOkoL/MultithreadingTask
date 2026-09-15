package com.task.port.state;

import com.task.port.entity.Ship;
import com.task.port.manager.BerthManager;
import com.task.port.manager.PortWarehouse;

public class ShipStateFactory {

    private final Ship ship;

    public ShipStateFactory(Ship ship) {
        this.ship = ship;
    }

    public ShipState createWaitingState() {
        return new WaitingState(ship, this);
    }

    public ShipState createDockingState(BerthManager berthManager, String berthId) {
        return new DockingState(ship, this, berthManager, berthId);
    }

    public ShipState createUnloadingState(PortWarehouse warehouse, String berthId) {
        return new UnloadingState(ship, this, warehouse, berthId);
    }

    public ShipState createLoadingState(PortWarehouse warehouse, String berthId) {
        return new LoadingState(ship, this, warehouse, berthId);
    }

    public ShipState createDepartingState(BerthManager berthManager, String berthId) {
        return new DepartingState(ship, this, berthManager, berthId);
    }

    public ShipState createServicedState() {
        return new ServicedState(ship, this);
    }
}

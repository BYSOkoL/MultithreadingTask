package com.port.entity;

import com.port.resource.BerthManager;
import com.port.resource.PortWarehouse;
import com.port.state.ShipState;
import com.port.state.WaitingState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Ship implements Runnable {

    private static final Logger logger = LogManager.getLogger(Ship.class);

    private final String id;
    private final int capacity;
    private final List<Container> cargo;
    private final int containersToLoad;
    private final List<Container> loaded = new ArrayList<>();
    private final BerthManager berths;
    private final PortWarehouse warehouse;
    private ShipState state;
    private boolean serviced;

    public Ship(String id, int capacity, List<Container> cargo, int containersToLoad,
                BerthManager berths, PortWarehouse warehouse) {
        this.id = id;
        this.capacity = capacity;
        this.cargo = new ArrayList<>(cargo);
        this.containersToLoad = containersToLoad;
        this.berths = berths;
        this.warehouse = warehouse;
        this.state = new WaitingState(this);
    }

    @Override
    public void run() {
        try {
            while (!serviced) {
                state.handle();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("{} was interrupted", id);
        }
    }

    public String getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getContainersToLoad() {
        return containersToLoad;
    }

    public BerthManager getBerths() {
        return berths;
    }

    public PortWarehouse getWarehouse() {
        return warehouse;
    }

    public boolean isServiced() {
        return serviced;
    }

    public void setServiced(boolean serviced) {
        this.serviced = serviced;
    }

    public void setState(ShipState state) {
        this.state = state;
    }

    public boolean hasCargo() {
        return !cargo.isEmpty();
    }

    public Container takeNextCargo() {
        return cargo.remove(0);
    }

    public void addLoaded(Container container) {
        loaded.add(container);
    }

    public int getLoadedCount() {
        return loaded.size();
    }
}
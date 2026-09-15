package com.task.port.entity;

import com.task.port.state.ShipState;
import com.task.port.state.ShipStateFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Ship implements Runnable {

    private static final Logger logger = LogManager.getLogger(Ship.class);

    private final String id;
    private final String name;
    private final int capacity;
    private final List<Container> containersToUnload;
    private final int containersToLoadCount;
    private final List<Container> loadedContainers;
    private final List<Container> unloadedContainers;
    private ShipState state;
    private final ShipStateFactory stateFactory;
    private final ReentrantLock lock;
    private boolean serviced;

    public Ship(String id, String name, int capacity,
                List<Container> containersToUnload, int containersToLoadCount) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.containersToUnload = new ArrayList<>(containersToUnload);
        this.containersToLoadCount = containersToLoadCount;
        this.loadedContainers = new ArrayList<>();
        this.unloadedContainers = new ArrayList<>();
        this.stateFactory = new ShipStateFactory(this);
        this.state = stateFactory.createWaitingState();
        this.lock = new ReentrantLock();
        this.serviced = false;
    }

    @Override
    public void run() {
        logger.info("Ship {} ({}) started", name, id);
        try {
            while (true) {
                lock.lock();
                try {
                    if (serviced) {
                        break;
                    }
                    state.handle();
                } finally {
                    lock.unlock();
                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Ship {} ({}) interrupted", name, id);
        }
        logger.info("Ship {} ({}) finished", name, id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Container> getContainersToUnload() {
        return new ArrayList<>(containersToUnload);
    }

    public int getContainersToLoadCount() {
        return containersToLoadCount;
    }

    public List<Container> getLoadedContainers() {
        return new ArrayList<>(loadedContainers);
    }

    public List<Container> getUnloadedContainers() {
        return new ArrayList<>(unloadedContainers);
    }

    public ShipState getState() {
        return state;
    }

    public void setState(ShipState state) {
        this.state = state;
    }

    public ShipStateFactory getStateFactory() {
        return stateFactory;
    }

    public boolean isServiced() {
        return serviced;
    }

    public void setServiced(boolean serviced) {
        this.serviced = serviced;
    }

    public void addLoadedContainer(Container container) {
        loadedContainers.add(container);
    }

    public void addUnloadedContainer(Container container) {
        unloadedContainers.add(container);
    }

    public void removeContainerToUnload(Container container) {
        containersToUnload.remove(container);
    }

    public boolean hasContainersToUnload() {
        return !containersToUnload.isEmpty();
    }

    public int getAvailableSpace() {
        return capacity - loadedContainers.size();
    }

    public int getLoadedCount() {
        return loadedContainers.size();
    }

    public ReentrantLock getLock() {
        return lock;
    }
}

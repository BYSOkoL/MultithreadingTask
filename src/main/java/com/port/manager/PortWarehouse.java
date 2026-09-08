package com.port.manager;

import com.port.entity.Container;
import com.port.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class PortWarehouse {

    private static final Logger logger = LogManager.getLogger(PortWarehouse.class);
    private static final PortWarehouse instance;

    static {
        instance = new PortWarehouse();
    }

    private final Queue<Container> containers;
    private final ReentrantLock lock;
    private int capacity;
    private int currentContainers;

    private PortWarehouse() {
        this.containers = new LinkedList<>();
        this.capacity = 100;
        this.currentContainers = 0;
        this.lock = new ReentrantLock();
    }

    public static PortWarehouse getInstance() {
        return instance;
    }

    public void initialize(int capacity, List<Container> initialContainers) {
        lock.lock();
        try {
            this.capacity = capacity;
            this.containers.clear();
            this.currentContainers = 0;
            for (Container container : initialContainers) {
                containers.offer(container);
                currentContainers++;
            }
            logger.info("Warehouse initialized with capacity {} and {} initial containers",
                    capacity, currentContainers);
        } finally {
            lock.unlock();
        }
    }

    public boolean addContainer(Container container) {
        lock.lock();
        try {
            if (currentContainers < capacity) {
                containers.offer(container);
                currentContainers++;
                logger.info("Container {} added to warehouse. Total: {}/{}",
                        container.getId(), currentContainers, capacity);
                return true;
            }
            logger.warn("Warehouse is full. Cannot add container {}", container.getId());
            return false;
        } finally {
            lock.unlock();
        }
    }

    public Container getContainerForShip(Ship ship) {
        lock.lock();
        try {
            if (ship.getAvailableSpace() <= 0) {
                return null;
            }
            if (containers.isEmpty()) {
                return null;
            }
            Container container = containers.poll();
            if (container != null) {
                currentContainers--;
                logger.info("Container {} removed from warehouse for ship {}. Remaining: {}/{}",
                        container.getId(), ship.getName(), currentContainers, capacity);
            }
            return container;
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentContainers() {
        lock.lock();
        try {
            return currentContainers;
        } finally {
            lock.unlock();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableSpace() {
        lock.lock();
        try {
            return capacity - currentContainers;
        } finally {
            lock.unlock();
        }
    }

    public List<Container> getAllContainers() {
        lock.lock();
        try {
            return new ArrayList<>(containers);
        } finally {
            lock.unlock();
        }
    }
}

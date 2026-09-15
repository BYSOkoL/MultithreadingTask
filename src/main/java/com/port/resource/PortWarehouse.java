package com.port.resource;

import com.port.entity.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class PortWarehouse {

    private static final Logger logger = LogManager.getLogger(PortWarehouse.class);
    private static final PortWarehouse INSTANCE = new PortWarehouse();

    private final List<Container> containers = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private int capacity;

    private PortWarehouse() {
    }

    public static PortWarehouse getInstance() {
        return INSTANCE;
    }

    public void init(int capacity, int initialStock) {
        lock.lock();
        try {
            this.capacity = capacity;
            containers.clear();
            for (int i = 1; i <= initialStock; i++) {
                containers.add(new Container("STOCK-" + i));
            }
            logger.info("Warehouse initialized: capacity={}, stock={}", capacity, initialStock);
        } finally {
            lock.unlock();
        }
    }

    public boolean store(Container container) {
        lock.lock();
        try {
            if (containers.size() >= capacity) {
                return false;
            }
            containers.add(container);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public Container take() {
        lock.lock();
        try {
            if (containers.isEmpty()) {
                return null;
            }
            return containers.remove(containers.size() - 1);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return containers.size();
        } finally {
            lock.unlock();
        }
    }

    public int getCapacity() {
        lock.lock();
        try {
            return capacity;
        } finally {
            lock.unlock();
        }
    }
}
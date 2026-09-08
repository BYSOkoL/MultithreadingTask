package com.port.repository;

import com.port.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ShipRepository {

    private static final Logger logger = LogManager.getLogger(ShipRepository.class);
    private final Map<String, Ship> ships;
    private final ReentrantLock lock;

    public ShipRepository() {
        this.ships = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public void add(Ship ship) {
        lock.lock();
        try {
            ships.put(ship.getId(), ship);
            logger.info("Ship {} ({}) registered", ship.getName(), ship.getId());
        } finally {
            lock.unlock();
        }
    }

    public Ship findById(String id) {
        lock.lock();
        try {
            return ships.get(id);
        } finally {
            lock.unlock();
        }
    }

    public List<Ship> findAll() {
        lock.lock();
        try {
            return new ArrayList<>(ships.values());
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return ships.size();
        } finally {
            lock.unlock();
        }
    }

    public List<Ship> getServicedShips() {
        lock.lock();
        try {
            List<Ship> result = new ArrayList<>();
            for (Ship ship : ships.values()) {
                if (ship.isServiced()) {
                    result.add(ship);
                }
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    public List<Ship> getActiveShips() {
        lock.lock();
        try {
            List<Ship> result = new ArrayList<>();
            for (Ship ship : ships.values()) {
                if (!ship.isServiced()) {
                    result.add(ship);
                }
            }
            return result;
        } finally {
            lock.unlock();
        }
    }
}

package com.task.port.manager;

import com.task.port.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class BerthManager {
    private static final Logger logger = LogManager.getLogger(BerthManager.class);

    private static final BerthManager instance;

    static {
        instance = new BerthManager();
    }

    private final Map<String, Ship> berths;
    private Semaphore availableBerths;
    private final ReentrantLock lock;
    private int totalBerths;
    private int nextBerthNumber;

    private BerthManager() {
        this.berths = new HashMap<>();
        this.totalBerths = 3;
        this.availableBerths = new Semaphore(totalBerths);
        this.lock = new ReentrantLock();
        this.nextBerthNumber = 1;
    }

    public static BerthManager getInstance() {
        return instance;
    }

    public void initialize(int numBerths) {
        lock.lock();
        try {
            this.totalBerths = numBerths;
            this.berths.clear();
            this.nextBerthNumber = 1;
            this.availableBerths = new Semaphore(numBerths);
            logger.info("BerthManager reinitialized with {} berths", numBerths);
        } finally {
            lock.unlock();
        }
    }

    public String acquireBerth(Ship ship) throws InterruptedException {
        availableBerths.acquire();
        lock.lock();
        try {
            String berthId = "Berth-" + nextBerthNumber;
            nextBerthNumber++;
            berths.put(berthId, ship);
            logger.info("Ship {} ({}) acquired {}", ship.getName(), ship.getId(), berthId);
            return berthId;
        } finally {
            lock.unlock();
        }
    }

    public void releaseBerth(Ship ship, String berthId) {
        lock.lock();
        try {
            berths.remove(berthId);
            logger.info("Ship {} ({}) released {}", ship.getName(), ship.getId(), berthId);
        } finally {
            lock.unlock();
        }
        availableBerths.release();
    }

    public int getOccupiedBerths() {
        lock.lock();
        try {
            return berths.size();
        } finally {
            lock.unlock();
        }
    }

    public int getAvailableBerths() {
        return availableBerths.availablePermits();
    }

    public int getTotalBerths() {
        return totalBerths;
    }
}
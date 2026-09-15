package com.port.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Semaphore;

public class BerthManager {

    private static final Logger logger = LogManager.getLogger(BerthManager.class);
    private final Semaphore semaphore;

    public BerthManager(int berths) {
        this.semaphore = new Semaphore(berths, true);
        logger.info("Port has {} berths", berths);
    }

    public void occupy() throws InterruptedException {
        semaphore.acquire();
        logger.info("Berth occupied, free berths: {}", semaphore.availablePermits());
    }

    public void free() {
        semaphore.release();
        logger.info("Berth freed, free berths: {}", semaphore.availablePermits());
    }
}
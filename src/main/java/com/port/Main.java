package com.port;

import com.port.config.PortConfig;
import com.port.config.PortConfigReader;
import com.port.entity.Container;
import com.port.entity.Ship;
import com.port.resource.BerthManager;
import com.port.resource.PortWarehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String CONFIG_PATH = "data/port_config.txt";

    public static void main(String[] args) {
        PortConfig config = new PortConfigReader(CONFIG_PATH).read();
        PortWarehouse warehouse = PortWarehouse.getInstance();
        warehouse.init(config.warehouseCapacity(), config.warehouseInitialStock());
        BerthManager berths = new BerthManager(config.berths());

        List<Ship> ships = createShips(config, berths, warehouse);
        ExecutorService pool = Executors.newFixedThreadPool(config.ships());
        for (Ship ship : ships) {
            pool.submit(ship);
        }
        pool.shutdown();
        awaitShips(pool);

        long serviced = ships.stream().filter(Ship::isServiced).count();
        logger.info("Port finished: {}/{} ships serviced, warehouse {}/{}",
                serviced, ships.size(), warehouse.size(), warehouse.getCapacity());
    }

    private static List<Ship> createShips(PortConfig config, BerthManager berths, PortWarehouse warehouse) {
        List<Ship> ships = new ArrayList<>();
        for (int i = 1; i <= config.ships(); i++) {
            List<Container> cargo = new ArrayList<>();
            for (int j = 1; j <= config.containersToUnload(); j++) {
                cargo.add(new Container("SHIP-" + i + "-C" + j));
            }
            ships.add(new Ship("Ship-" + i, config.shipCapacity(), cargo,
                    config.containersToLoad(), berths, warehouse));
        }
        return ships;
    }

    private static void awaitShips(ExecutorService pool) {
        try {
            if (!pool.awaitTermination(2, TimeUnit.MINUTES)) {
                logger.warn("Ships did not finish in time");
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
package com.port.state;

import com.port.entity.Container;
import com.port.entity.Ship;
import com.port.manager.BerthManager;
import com.port.manager.PortWarehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class LoadingState implements ShipState {
    private static final Logger logger = LogManager.getLogger(LoadingState.class);

    private final Ship ship;
    private final ShipStateFactory stateFactory;
    private final PortWarehouse warehouse;
    private final String berthId;

    public LoadingState(Ship ship, ShipStateFactory stateFactory,
                        PortWarehouse warehouse, String berthId) {
        this.ship = ship;
        this.stateFactory = stateFactory;
        this.warehouse = warehouse;
        this.berthId = berthId;
    }

    @Override
    public void handle() {
        logger.info("Ship {} ({}) is loading containers at {}",
                ship.getName(), ship.getId(), berthId);
        try {
            int targetLoad = ship.getContainersToLoadCount();
            int emptyAttempts = 0;
            int maxEmptyAttempts = 100; // ✅ Увеличили с 3 до 100 — корабль будет ждать,
            // пока другие корабли разгрузятся и заполнят склад

            while (ship.getLoadedCount() < targetLoad && ship.getAvailableSpace() > 0) {
                Container container = warehouse.getContainerForShip(ship);
                if (container != null) {
                    ship.addLoadedContainer(container);
                    logger.info("Loaded container {} from warehouse to ship {}",
                            container.getId(), ship.getName());
                    TimeUnit.MILLISECONDS.sleep(300);
                    emptyAttempts = 0;
                } else {
                    emptyAttempts++;
                    if (emptyAttempts >= maxEmptyAttempts) {
                        logger.warn("Warehouse empty for too long, ship {} ({}) stops loading with {}/{} containers",
                                ship.getName(), ship.getId(), ship.getLoadedCount(), targetLoad);
                        break;
                    }
                    if (emptyAttempts % 10 == 0) { // ✅ Логируем не каждую секунду, а каждые 10
                        logger.warn("Waiting for containers in warehouse for ship {} (attempt {}/{})",
                                ship.getName(), emptyAttempts, maxEmptyAttempts);
                    }
                    TimeUnit.SECONDS.sleep(1);
                }
            }
            ship.setState(stateFactory.createDepartingState(
                    BerthManager.getInstance(), berthId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Ship {} ({}) interrupted while loading", ship.getName(), ship.getId());
        }
    }

    @Override
    public String getStateName() {
        return "LOADING";
    }
}
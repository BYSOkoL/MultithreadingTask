package com.port.observer;

import com.port.manager.BerthManager;
import com.port.manager.PortWarehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsObserverTest {

    private StatisticsObserver observer;

    @BeforeEach
    void setUp() {
        BerthManager berthManager = BerthManager.getInstance();
        berthManager.initialize(3);
        PortWarehouse warehouse = PortWarehouse.getInstance();
        warehouse.initialize(100, new ArrayList<>());
        observer = new StatisticsObserver(berthManager, warehouse);
    }

    @Test
    void shouldCountServicedShips() {
        // when
        observer.update("Ship SHIP-1 SERVICED");
        observer.update("Ship SHIP-2 SERVICED");

        // then
        assertEquals(2, observer.getTotalShipsServiced());
    }

    @Test
    void shouldCountLoadedContainers() {
        // when
        observer.update("Loaded container CONT-1");
        observer.update("Loaded container CONT-2");
        observer.update("Loaded container CONT-3");

        // then
        assertEquals(3, observer.getTotalContainersLoaded());
    }

    @Test
    void shouldCountUnloadedContainers() {
        // when
        observer.update("Unloaded container CONT-1");
        observer.update("Unloaded container CONT-2");

        // then
        assertEquals(2, observer.getTotalContainersUnloaded());
    }
}

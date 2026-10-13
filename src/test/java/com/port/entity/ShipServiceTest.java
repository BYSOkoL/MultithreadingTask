package com.port.entity;

import com.port.resource.BerthManager;
import com.port.resource.PortWarehouse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipServiceTest {

    @Test
    void shouldServiceShipFromStartToFinish() {
        // given
        PortWarehouse warehouse = PortWarehouse.getInstance();
        warehouse.init(10, 5);
        BerthManager berths = new BerthManager(1);
        List<Container> cargo = List.of(new Container("C-1"), new Container("C-2"));
        Ship ship = new Ship("Ship-1", 5, cargo, 3, berths, warehouse);

        // when
        ship.run();

        // then
        assertTrue(ship.isServiced());
        assertEquals(3, ship.getLoadedCount());
    }
}
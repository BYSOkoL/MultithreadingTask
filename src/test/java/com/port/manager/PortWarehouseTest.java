package com.port.manager;

import com.task.port.entity.Container;
import com.task.port.entity.Ship;
import com.task.port.manager.PortWarehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortWarehouseTest {

    private PortWarehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = PortWarehouse.getInstance();
        warehouse.initialize(10, new ArrayList<>());
    }

    @Test
    void shouldAddContainer() {
        // given
        Container container = new Container("CONT-1");

        // when
        boolean added = warehouse.addContainer(container);

        // then
        assertTrue(added);
        assertEquals(1, warehouse.getCurrentContainers());
    }

    @Test
    void shouldGetContainerForShip() {
        // given
        Container container = new Container("CONT-1");
        warehouse.addContainer(container);
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 1);

        // when
        Container retrieved = warehouse.getContainerForShip(ship);

        // then
        assertNotNull(retrieved);
        assertEquals("CONT-1", retrieved.getId());
        assertEquals(0, warehouse.getCurrentContainers());
    }

    @Test
    void shouldRespectCapacity() {
        // given
        warehouse.initialize(2, new ArrayList<>());

        // when
        boolean added1 = warehouse.addContainer(new Container("CONT-1"));
        boolean added2 = warehouse.addContainer(new Container("CONT-2"));
        boolean added3 = warehouse.addContainer(new Container("CONT-3"));

        // then
        assertTrue(added1);
        assertTrue(added2);
        assertFalse(added3);
        assertEquals(2, warehouse.getCurrentContainers());
    }

    @Test
    void shouldReturnNullWhenEmpty() {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 1);

        // when
        Container retrieved = warehouse.getContainerForShip(ship);

        // then
        assertNull(retrieved);
    }

    @Test
    void shouldReturnNullWhenShipFull() {
        // given
        warehouse.initialize(10, new ArrayList<>());
        warehouse.addContainer(new Container("CONT-1"));
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 1, unload, 1);
        ship.addLoadedContainer(new Container("FULL-1"));

        // when
        Container retrieved = warehouse.getContainerForShip(ship);

        // then
        assertNull(retrieved);
        assertEquals(1, warehouse.getCurrentContainers());
    }

    @Test
    void shouldInitializeWithContainers() {
        // given
        List<Container> initial = new ArrayList<>();
        initial.add(new Container("INIT-1"));
        initial.add(new Container("INIT-2"));

        // when
        warehouse.initialize(10, initial);

        // then
        assertEquals(2, warehouse.getCurrentContainers());
    }
}

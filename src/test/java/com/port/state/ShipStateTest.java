package com.port.state;

import com.task.port.entity.Container;
import com.task.port.entity.Ship;
import com.task.port.state.ShipState;
import com.task.port.state.ShipStateFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipStateTest {

    @Test
    void shouldCreateWaitingState() {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);

        // when
        ShipState state = ship.getState();

        // then
        assertNotNull(state);
        assertEquals("WAITING", state.getStateName());
    }

    @Test
    void shouldTransitionToServicedState() {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);
        ShipStateFactory factory = ship.getStateFactory();

        // when
        ship.setState(factory.createServicedState());
        ship.getState().handle();

        // then
        assertEquals("SERVICED", ship.getState().getStateName());
        assertTrue(ship.isServiced());
    }

    @Test
    void shouldNotBeServicedInitially() {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);

        // then
        assertFalse(ship.isServiced());
    }

    @Test
    void shouldTrackLoadedContainers() {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);
        Container container = new Container("CONT-1");

        // when
        ship.addLoadedContainer(container);

        // then
        assertEquals(1, ship.getLoadedCount());
        assertEquals(19, ship.getAvailableSpace());
    }

    @Test
    void shouldTrackUnloadedContainers() {
        // given
        List<Container> unload = new ArrayList<>();
        unload.add(new Container("CONT-1"));
        unload.add(new Container("CONT-2"));
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);

        // then
        assertTrue(ship.hasContainersToUnload());
        assertEquals(2, ship.getContainersToUnload().size());
    }
}

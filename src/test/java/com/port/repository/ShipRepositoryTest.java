package com.port.repository;

import com.port.entity.Container;
import com.port.entity.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ShipRepositoryTest {

    private ShipRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ShipRepository();
    }

    @Test
    void shouldAddShip() {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);

        // when
        repository.add(ship);

        // then
        assertNotNull(repository.findById("SHIP-1"));
        assertEquals(1, repository.size());
    }

    @Test
    void shouldFindShipById() {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);
        repository.add(ship);

        // when
        Ship found = repository.findById("SHIP-1");

        // then
        assertNotNull(found);
        assertEquals("SHIP-1", found.getId());
    }

    @Test
    void shouldReturnNullForUnknownId() {
        // when
        Ship found = repository.findById("UNKNOWN");

        // then
        assertNull(found);
    }

    @Test
    void shouldGetServicedShips() {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship1 = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);
        Ship ship2 = new Ship("SHIP-2", "Vessel-2", 20, unload, 0);
        repository.add(ship1);
        repository.add(ship2);
        ship1.setServiced(true);

        // when
        List<Ship> serviced = repository.getServicedShips();

        // then
        assertEquals(1, serviced.size());
        assertEquals("SHIP-1", serviced.get(0).getId());
    }

    @Test
    void shouldGetActiveShips() {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship1 = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);
        Ship ship2 = new Ship("SHIP-2", "Vessel-2", 20, unload, 0);
        repository.add(ship1);
        repository.add(ship2);
        ship1.setServiced(true);

        // when
        List<Ship> active = repository.getActiveShips();

        // then
        assertEquals(1, active.size());
        assertEquals("SHIP-2", active.get(0).getId());
    }
}

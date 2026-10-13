package com.port.resource;

import com.port.entity.Container;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        warehouse.init(2, 1);
    }

    @Test
    void shouldStoreWithinCapacity() {
        // given
        Container container = new Container("C-1");

        // when
        boolean stored = warehouse.store(container);

        // then
        assertTrue(stored);
        assertEquals(2, warehouse.size());
    }

    @Test
    void shouldRejectOverCapacity() {
        // given
        warehouse.store(new Container("C-1"));

        // when
        boolean stored = warehouse.store(new Container("C-2"));

        // then
        assertFalse(stored);
        assertEquals(2, warehouse.size());
    }

    @Test
    void shouldTakeFromStock() {
        // when
        Container container = warehouse.take();

        // then
        assertNotNull(container);
        assertEquals(0, warehouse.size());
    }

    @Test
    void shouldReturnNullWhenEmpty() {
        // given
        warehouse.take();

        // when
        Container container = warehouse.take();

        // then
        assertNull(container);
    }
}
package com.port.manager;

import com.port.entity.Container;
import com.port.entity.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BerthManagerTest {

    private BerthManager berthManager;

    @BeforeEach
    void setUp() {
        berthManager = BerthManager.getInstance();
        berthManager.initialize(3);
    }

    @Test
    void shouldAcquireBerth() throws InterruptedException {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);

        // when
        String berthId = berthManager.acquireBerth(ship);

        // then
        assertNotNull(berthId);
        assertEquals(2, berthManager.getAvailableBerths());
        assertEquals(1, berthManager.getOccupiedBerths());
    }

    @Test
    void shouldReleaseBerth() throws InterruptedException {
        // given
        List<Container> unload = new ArrayList<>();
        Ship ship = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);
        String berthId = berthManager.acquireBerth(ship);

        // when
        berthManager.releaseBerth(ship, berthId);

        // then
        assertEquals(3, berthManager.getAvailableBerths());
        assertEquals(0, berthManager.getOccupiedBerths());
    }

    @Test
    void shouldLimitConcurrentBerths() throws InterruptedException {
        // given
        berthManager.initialize(2);
        List<Container> unload = new ArrayList<>();
        Ship ship1 = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);
        Ship ship2 = new Ship("SHIP-2", "Vessel-2", 20, unload, 0);

        // when
        String berth1 = berthManager.acquireBerth(ship1);
        String berth2 = berthManager.acquireBerth(ship2);

        // then
        assertNotNull(berth1);
        assertNotNull(berth2);
        assertEquals(0, berthManager.getAvailableBerths());
        assertEquals(2, berthManager.getOccupiedBerths());
    }

    @Test
    void shouldBlockWhenNoBerthsAvailable() throws InterruptedException {
        // given
        berthManager.initialize(1);
        List<Container> unload = new ArrayList<>();
        Ship ship1 = new Ship("SHIP-1", "Vessel-1", 20, unload, 0);
        Ship ship2 = new Ship("SHIP-2", "Vessel-2", 20, unload, 0);

        String berth1 = berthManager.acquireBerth(ship1);

        // when
        CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(() -> {
            try {
                String berth2 = berthManager.acquireBerth(ship2);
                assertNotNull(berth2);
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.start();

        TimeUnit.MILLISECONDS.sleep(200);

        // then: ship2 should still be waiting
        assertEquals(0, berthManager.getAvailableBerths());

        // when: release berth1
        berthManager.releaseBerth(ship1, berth1);

        // then: ship2 should acquire berth
        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }
}

package com.port.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContainerTest {

    @Test
    void shouldCreateContainer() {
        // given
        String id = "CONT-1";

        // when
        Container container = new Container(id);

        // then
        assertNotNull(container);
        assertEquals(id, container.getId());
    }

    @Test
    void shouldHaveToString() {
        // given
        Container container = new Container("CONT-1");

        // when
        String str = container.toString();

        // then
        assertEquals("Container{CONT-1}", str);
    }
}

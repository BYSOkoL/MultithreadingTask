package com.port.reader;

import com.task.port.exception.PortException;
import com.task.port.reader.PortConfigReader;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PortConfigReaderTest {

    @Test
    void shouldReadConfig() throws PortException {
        // given
        PortConfigReader reader = new PortConfigReader("data/port_config.txt");

        // when
        Map<String, Integer> config = reader.readConfig();

        // then
        assertNotNull(config);
        assertEquals(3, config.get("berths"));
        assertEquals(100, config.get("warehouse_capacity"));
        assertEquals(5, config.get("ships"));
    }
}

package com.port.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortConfigReader {

    private static final Logger logger = LogManager.getLogger(PortConfigReader.class);
    private final Path filePath;

    public PortConfigReader(String relativePath) {
        this.filePath = Paths.get(relativePath);
    }

    public PortConfig read() {
        Map<String, Integer> values = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                String[] parts = line.strip().split("=");
                if (parts.length == 2) {
                    values.put(parts[0].strip(), Integer.parseInt(parts[1].strip()));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read config file: " + filePath, e);
        }
        logger.info("Config loaded: {}", values);
        return new PortConfig(
                values.get("berths"),
                values.get("warehouse_capacity"),
                values.get("warehouse_initial_stock"),
                values.get("ships"),
                values.get("ship_capacity"),
                values.get("containers_to_unload"),
                values.get("containers_to_load"));
    }
}
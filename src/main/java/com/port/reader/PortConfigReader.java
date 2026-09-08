package com.port.reader;

import com.port.exception.PortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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

    public Map<String, Integer> readConfig() throws PortException {
        logger.info("Reading port configuration from file: {}", filePath);
        Map<String, Integer> config = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                String trimmedLine = line.strip();
                if (trimmedLine.isEmpty()) {
                    continue;
                }
                String[] parts = trimmedLine.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    int value = Integer.parseInt(parts[1].trim());
                    config.put(key, value);
                    logger.info("Configuration: {} = {}", key, value);
                }
            }
            return config;
        } catch (IOException e) {
            logger.error("Failed to read config file: {}", filePath, e);
            throw new PortException("Cannot read config file: " + filePath, e);
        }
    }
}

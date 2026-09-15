package com.task.port;

import com.task.port.reader.PortConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String CONFIG_FILE_PATH = "data/port_config.txt";

    public static void main(String[] args) {
        logger.info("Initializing port application...");
        try {
            PortConfigReader configReader = new PortConfigReader(CONFIG_FILE_PATH);
            Port port = new Port(configReader);
            logger.info("Starting port operations...");
            port.run();
            logger.info("Port operations completed");
        } catch (Exception e) {
            logger.error("Failed to start port: {}", e.getMessage(), e);
        }
    }
}

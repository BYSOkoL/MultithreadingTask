package com.port.config;

public record PortConfig(int berths,
                         int warehouseCapacity,
                         int warehouseInitialStock,
                         int ships,
                         int shipCapacity,
                         int containersToUnload,
                         int containersToLoad) {
}
package com.task.port.state;

public interface ShipState {
    void handle();
    String getStateName();
}

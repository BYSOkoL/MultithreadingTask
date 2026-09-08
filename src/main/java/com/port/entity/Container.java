package com.port.entity;

public class Container {

    private final String id;

    public Container(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Container{" + id + '}';
    }
}

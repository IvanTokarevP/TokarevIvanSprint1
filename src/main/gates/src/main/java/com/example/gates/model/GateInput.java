package com.example.gates.model;

public class GateInput {
    private String name;
    private String location;
    private String status; // опционально

    public GateInput() {}

    public GateInput(String name, String location, String status) {
        this.name = name;
        this.location = location;
        this.status = status;
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
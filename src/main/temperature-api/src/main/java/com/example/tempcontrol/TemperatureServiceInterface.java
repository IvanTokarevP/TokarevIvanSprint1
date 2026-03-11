package com.example.tempcontrol;

public interface TemperatureServiceInterface {
    String resolveLocation(String sensorID);
    String resolveSensorID(String location);
    double generateRandomTemperature();
    TemperatureService.TemperatureData getTemperatureData(String location);
}

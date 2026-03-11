package com.example.tempcontrol;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TemperatureService implements TemperatureServiceInterface {

    private final Random random = new Random();

    @Override
    public String resolveLocation(String sensorID) {
        if (sensorID != null && !sensorID.isEmpty()) {
                switch (sensorID) {
                    case "1":
                        return "Living Room";
                    case "2":
                        return "Bedroom";
                    case "3":
                        return "Kitchen";
                    default:
                        return "Unknown";
                }
        }
        return "Unknown";
    }

    @Override
    public String resolveSensorID(String location) {
        if (location != null && !location.isEmpty()) {
            switch (location) {
                case "Living Room":
                    return "1";
                case "Bedroom":
                    return "2";
                case "Kitchen":
                    return "3";
                default:
                    return "0";
            }
        }
        return "0";
    }

    @Override
    public double generateRandomTemperature() {
        return -10 + random.nextDouble() * 45;
    }

    @Override
    public TemperatureData getTemperatureData(String location) {
            String resolvedSensorID = resolveSensorID(location);
            double temperature = generateRandomTemperature();
            return new TemperatureData(temperature);
    }

    public static class TemperatureData {

        private final double value;

        public TemperatureData(double temperature) {
            this.value = temperature;
        }



        public double getValue() {
            return value;
        }
    }
}

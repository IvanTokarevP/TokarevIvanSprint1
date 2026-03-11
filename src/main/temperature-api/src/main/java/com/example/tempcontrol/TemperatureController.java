package com.example.tempcontrol;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemperatureController {

    private final TemperatureServiceInterface temperatureService;

    public TemperatureController(TemperatureServiceInterface temperatureService) {
        this.temperatureService = temperatureService;
    }

    @GetMapping("/temperature")
    public TemperatureService.TemperatureData getTemperature(
            @RequestParam(required = false) String location) {
        return temperatureService.getTemperatureData(location);
    }
}

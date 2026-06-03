package com.example.weather.model;

import java.util.List;

/** Full result for one location: current conditions + multi-day forecast. */
public class WeatherResult {

    private final Location location;
    private final CurrentConditions current;
    private final List<DailyForecast> days;

    public WeatherResult(Location location, CurrentConditions current, List<DailyForecast> days) {
        this.location = location;
        this.current = current;
        this.days = days;
    }

    public Location getLocation() {
        return location;
    }

    public CurrentConditions getCurrent() {
        return current;
    }

    public List<DailyForecast> getDays() {
        return days;
    }
}

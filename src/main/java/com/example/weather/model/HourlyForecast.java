package com.example.weather.model;

import java.time.LocalDateTime;

/** A single hourly data point. */
public class HourlyForecast {

    private final LocalDateTime time;
    private final double temperature;
    private final int weatherCode;

    public HourlyForecast(LocalDateTime time, double temperature, int weatherCode) {
        this.time = time;
        this.temperature = temperature;
        this.weatherCode = weatherCode;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getWeatherCode() {
        return weatherCode;
    }
}

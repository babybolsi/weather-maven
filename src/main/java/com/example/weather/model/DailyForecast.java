package com.example.weather.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Aggregated forecast for a single day, including its hourly breakdown. */
public class DailyForecast {

    private final LocalDate date;
    private final double tempMax;
    private final double tempMin;
    private final int weatherCode;
    private final List<HourlyForecast> hours = new ArrayList<>();

    public DailyForecast(LocalDate date, double tempMax, double tempMin, int weatherCode) {
        this.date = date;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.weatherCode = weatherCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTempMax() {
        return tempMax;
    }

    public double getTempMin() {
        return tempMin;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public List<HourlyForecast> getHours() {
        return hours;
    }

    public void addHour(HourlyForecast hour) {
        hours.add(hour);
    }
}

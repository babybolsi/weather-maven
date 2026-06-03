package com.example.weather.model;

/** Current weather snapshot. */
public class CurrentConditions {

    private final double temperature;
    private final int humidity;
    private final double windSpeed;
    private final int weatherCode;

    public CurrentConditions(double temperature, int humidity, double windSpeed, int weatherCode) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.weatherCode = weatherCode;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWeatherCode() {
        return weatherCode;
    }
}

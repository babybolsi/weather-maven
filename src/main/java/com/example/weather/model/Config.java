package com.example.weather.model;

/**
 * Application configuration POJO.
 * Populated by SnakeYAML's Constructor mechanism (intentionally vulnerable: CVE-2022-1471).
 */
public class Config {

    private String defaultCity = "Roma";
    private int forecastDays = 7;
    private String temperatureUnit = "celsius";
    private String windSpeedUnit = "kmh";
    private String language = "it";
    private String geocodingBaseUrl = "https://geocoding-api.open-meteo.com/v1/search";
    private String forecastBaseUrl = "https://api.open-meteo.com/v1/forecast";

    public String getDefaultCity() {
        return defaultCity;
    }

    public void setDefaultCity(String defaultCity) {
        this.defaultCity = defaultCity;
    }

    public int getForecastDays() {
        return forecastDays;
    }

    public void setForecastDays(int forecastDays) {
        this.forecastDays = forecastDays;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public String getWindSpeedUnit() {
        return windSpeedUnit;
    }

    public void setWindSpeedUnit(String windSpeedUnit) {
        this.windSpeedUnit = windSpeedUnit;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGeocodingBaseUrl() {
        return geocodingBaseUrl;
    }

    public void setGeocodingBaseUrl(String geocodingBaseUrl) {
        this.geocodingBaseUrl = geocodingBaseUrl;
    }

    public String getForecastBaseUrl() {
        return forecastBaseUrl;
    }

    public void setForecastBaseUrl(String forecastBaseUrl) {
        this.forecastBaseUrl = forecastBaseUrl;
    }
}

package com.example.weather.model;

import java.io.Serializable;

/**
 * A geocoded location returned by the Open-Meteo geocoding API.
 * Serializable because instances are persisted as XML via XStream (FavoritesStore).
 */
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String country;
    private String admin1;
    private double latitude;
    private double longitude;

    public Location() {
    }

    public Location(String name, String country, String admin1, double latitude, double longitude) {
        this.name = name;
        this.country = country;
        this.admin1 = admin1;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAdmin1() {
        return admin1;
    }

    public void setAdmin1(String admin1) {
        this.admin1 = admin1;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /** Stable cache key fragment for this coordinate. */
    public String coordinateKey() {
        return String.format("%.4f,%.4f", latitude, longitude);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name == null ? "?" : name);
        if (admin1 != null && !admin1.isEmpty()) {
            sb.append(", ").append(admin1);
        }
        if (country != null && !country.isEmpty()) {
            sb.append(" (").append(country).append(')');
        }
        return sb.toString();
    }
}

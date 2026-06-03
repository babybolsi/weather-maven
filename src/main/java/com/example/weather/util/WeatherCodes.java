package com.example.weather.util;

import java.util.HashMap;
import java.util.Map;

/** Maps WMO weather interpretation codes (Open-Meteo) to a label + emoji. */
public final class WeatherCodes {

    private static final Map<Integer, String> LABELS = new HashMap<>();
    private static final Map<Integer, String> ICONS = new HashMap<>();

    static {
        put(0, "Sereno", "☀️");
        put(1, "Prevalentemente sereno", "🌤️");
        put(2, "Parzialmente nuvoloso", "⛅");
        put(3, "Coperto", "☁️");
        put(45, "Nebbia", "🌫️");
        put(48, "Nebbia con brina", "🌫️");
        put(51, "Pioviggine leggera", "🌦️");
        put(53, "Pioviggine moderata", "🌦️");
        put(55, "Pioviggine intensa", "🌧️");
        put(61, "Pioggia leggera", "🌧️");
        put(63, "Pioggia moderata", "🌧️");
        put(65, "Pioggia forte", "🌧️");
        put(71, "Neve leggera", "🌨️");
        put(73, "Neve moderata", "🌨️");
        put(75, "Neve forte", "❄️");
        put(80, "Rovesci leggeri", "🌦️");
        put(81, "Rovesci moderati", "🌧️");
        put(82, "Rovesci violenti", "⛈️");
        put(95, "Temporale", "⛈️");
        put(96, "Temporale con grandine", "⛈️");
        put(99, "Temporale forte con grandine", "⛈️");
    }

    private WeatherCodes() {
    }

    private static void put(int code, String label, String icon) {
        LABELS.put(code, label);
        ICONS.put(code, icon);
    }

    public static String label(int code) {
        return LABELS.getOrDefault(code, "Codice " + code);
    }

    public static String icon(int code) {
        return ICONS.getOrDefault(code, "❓");
    }
}

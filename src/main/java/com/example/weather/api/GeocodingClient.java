package com.example.weather.api;

import com.example.weather.model.Config;
import com.example.weather.model.Location;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resolves a place name to coordinates via the Open-Meteo geocoding API.
 *
 * <p>HTTP performed with Spring 5.3.16 {@link RestTemplate} (intentionally vulnerable: Spring4Shell
 * family) and JSON parsed with Gson 2.8.5 (intentionally vulnerable: CVE-2022-25647).
 */
public class GeocodingClient {

    private static final Logger LOG = LogManager.getLogger(GeocodingClient.class);

    private final Config config;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    public GeocodingClient(Config config) {
        this.config = config;
    }

    public List<Location> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(config.getGeocodingBaseUrl())
                .queryParam("name", query.trim())
                .queryParam("count", 8)
                .queryParam("language", config.getLanguage())
                .queryParam("format", "json")
                .build()
                .encode()
                .toUri();

        LOG.info("Geocoding search for '{}' -> {}", query, uri);
        String body = restTemplate.getForObject(uri, String.class);
        return parse(body);
    }

    private List<Location> parse(String body) {
        List<Location> out = new ArrayList<>();
        if (body == null) {
            return out;
        }
        JsonObject root = new JsonParser().parse(body).getAsJsonObject();
        if (!root.has("results") || root.get("results").isJsonNull()) {
            return out;
        }
        root.getAsJsonArray("results").forEach(el -> {
            JsonObject o = el.getAsJsonObject();
            Location loc = new Location(
                    getString(o, "name"),
                    getString(o, "country"),
                    getString(o, "admin1"),
                    o.get("latitude").getAsDouble(),
                    o.get("longitude").getAsDouble());
            out.add(loc);
        });
        LOG.info("Geocoding returned {} result(s)", out.size());
        return out;
    }

    private static String getString(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : "";
    }
}

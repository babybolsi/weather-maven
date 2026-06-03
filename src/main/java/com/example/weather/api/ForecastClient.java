package com.example.weather.api;

import com.example.weather.model.Config;
import com.example.weather.model.CurrentConditions;
import com.example.weather.model.DailyForecast;
import com.example.weather.model.HourlyForecast;
import com.example.weather.model.Location;
import com.example.weather.model.WeatherResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetches the multi-day / hourly forecast from Open-Meteo.
 *
 * <p>HTTP performed with Apache HttpClient 4.5.12 (intentionally vulnerable: CVE-2020-13956) and
 * JSON parsed with Jackson Databind 2.9.8 (intentionally vulnerable: ~35 deserialization CVEs).
 */
public class ForecastClient {

    private static final Logger LOG = LogManager.getLogger(ForecastClient.class);

    private final Config config;
    private final ObjectMapper mapper = new ObjectMapper();

    public ForecastClient(Config config) {
        this.config = config;
    }

    public WeatherResult fetch(Location location) throws Exception {
        URI uri = new URIBuilder(config.getForecastBaseUrl())
                .addParameter("latitude", String.valueOf(location.getLatitude()))
                .addParameter("longitude", String.valueOf(location.getLongitude()))
                .addParameter("current", "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code")
                .addParameter("hourly", "temperature_2m,weather_code")
                .addParameter("daily", "weather_code,temperature_2m_max,temperature_2m_min")
                .addParameter("timezone", "auto")
                .addParameter("forecast_days", String.valueOf(config.getForecastDays()))
                .build();

        LOG.info("Forecast fetch -> {}", uri);
        String body;
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(uri))) {
            body = EntityUtils.toString(response.getEntity());
        }
        return parse(location, body);
    }

    private WeatherResult parse(Location location, String body) throws Exception {
        JsonNode root = mapper.readTree(body);

        JsonNode cur = root.path("current");
        CurrentConditions current = new CurrentConditions(
                cur.path("temperature_2m").asDouble(),
                cur.path("relative_humidity_2m").asInt(),
                cur.path("wind_speed_10m").asDouble(),
                cur.path("weather_code").asInt());

        // Build the day map first.
        Map<LocalDate, DailyForecast> byDate = new LinkedHashMap<>();
        JsonNode daily = root.path("daily");
        JsonNode dDates = daily.path("time");
        JsonNode dMax = daily.path("temperature_2m_max");
        JsonNode dMin = daily.path("temperature_2m_min");
        JsonNode dCode = daily.path("weather_code");
        for (int i = 0; i < dDates.size(); i++) {
            LocalDate date = LocalDate.parse(dDates.get(i).asText());
            byDate.put(date, new DailyForecast(date,
                    dMax.get(i).asDouble(), dMin.get(i).asDouble(), dCode.get(i).asInt()));
        }

        // Distribute hourly points into their day.
        JsonNode hourly = root.path("hourly");
        JsonNode hTimes = hourly.path("time");
        JsonNode hTemp = hourly.path("temperature_2m");
        JsonNode hCode = hourly.path("weather_code");
        for (int i = 0; i < hTimes.size(); i++) {
            LocalDateTime time = LocalDateTime.parse(hTimes.get(i).asText());
            DailyForecast day = byDate.get(time.toLocalDate());
            if (day != null) {
                day.addHour(new HourlyForecast(time, hTemp.get(i).asDouble(), hCode.get(i).asInt()));
            }
        }

        List<DailyForecast> days = new ArrayList<>(byDate.values());
        LOG.info("Parsed forecast: {} day(s) for {}", days.size(), location.getName());
        return new WeatherResult(location, current, days);
    }
}

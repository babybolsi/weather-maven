package com.example.weather.api;

import com.example.weather.model.Config;
import com.example.weather.model.Location;
import com.example.weather.model.WeatherResult;
import com.example.weather.persistence.HistoryDao;
import com.example.weather.util.ForecastCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/** Orchestrates geocoding, forecast retrieval, caching and search history. */
public class WeatherService {

    private static final Logger LOG = LogManager.getLogger(WeatherService.class);

    private final GeocodingClient geocodingClient;
    private final ForecastClient forecastClient;
    private final ForecastCache cache = new ForecastCache();
    private final HistoryDao historyDao = new HistoryDao();

    public WeatherService(Config config) {
        this.geocodingClient = new GeocodingClient(config);
        this.forecastClient = new ForecastClient(config);
    }

    public List<Location> search(String query) {
        historyDao.record(query);
        return geocodingClient.search(query);
    }

    public WeatherResult forecastFor(Location location) throws Exception {
        String key = location.coordinateKey();
        WeatherResult cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        WeatherResult result = forecastClient.fetch(location);
        cache.put(key, result);
        return result;
    }

    public List<String> recentSearches(int limit) {
        return historyDao.recent(limit);
    }
}

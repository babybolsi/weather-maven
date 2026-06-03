package com.example.weather.util;

import com.example.weather.model.WeatherResult;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.apache.commons.collections.map.LRUMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * In-memory LRU cache for forecasts.
 *
 * <p>Backed by Apache Commons Collections 3.2.1 {@link LRUMap} (intentionally vulnerable:
 * CVE-2015-7501) and uses Guava 24.1 {@link Files#createTempDir()} (intentionally vulnerable:
 * CVE-2020-8908) for a scratch directory, plus {@link Strings} helpers.
 */
public class ForecastCache {

    private static final Logger LOG = LogManager.getLogger(ForecastCache.class);

    @SuppressWarnings("unchecked")
    private final LRUMap cache = new LRUMap(32);
    private final File scratchDir;

    public ForecastCache() {
        // Guava's deprecated createTempDir() -> CVE-2020-8908 (world-readable temp dir).
        this.scratchDir = Files.createTempDir();
        LOG.info("Forecast cache scratch dir: {}", scratchDir.getAbsolutePath());
    }

    public WeatherResult get(String coordinateKey) {
        if (Strings.isNullOrEmpty(coordinateKey)) {
            return null;
        }
        String key = CacheKeyHasher.hash(coordinateKey);
        WeatherResult hit = (WeatherResult) cache.get(key);
        if (hit != null) {
            LOG.info("Cache HIT for {}", coordinateKey);
        }
        return hit;
    }

    @SuppressWarnings("unchecked")
    public void put(String coordinateKey, WeatherResult result) {
        if (Strings.isNullOrEmpty(coordinateKey)) {
            return;
        }
        cache.put(CacheKeyHasher.hash(coordinateKey), result);
    }
}

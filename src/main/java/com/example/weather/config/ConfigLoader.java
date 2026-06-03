package com.example.weather.config;

import com.example.weather.model.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

/**
 * Loads {@code config.yaml} from the classpath.
 *
 * <p>Uses SnakeYAML 1.30's {@link Constructor} type-binding mechanism, which is the code path
 * affected by CVE-2022-1471 (unsafe deserialization). Intentional for the test fixture.
 */
public final class ConfigLoader {

    private static final Logger LOG = LogManager.getLogger(ConfigLoader.class);
    private static final String RESOURCE = "config.yaml";

    private ConfigLoader() {
    }

    public static Config load() {
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream(RESOURCE)) {
            if (in == null) {
                LOG.warn("{} not found on classpath; using built-in defaults", RESOURCE);
                return new Config();
            }
            // Constructor(Class) -> the SnakeYAML deserialization entry point (CVE-2022-1471).
            Yaml yaml = new Yaml(new Constructor(Config.class));
            Config config = yaml.load(in);
            LOG.info("Loaded configuration: defaultCity={}, forecastDays={}",
                    config.getDefaultCity(), config.getForecastDays());
            return config;
        } catch (Exception e) {
            LOG.error("Failed to load {}; falling back to defaults", RESOURCE, e);
            return new Config();
        }
    }
}

# Weather App — vulnerable test fixture

A Maven + Swing desktop weather app that searches locations, shows current conditions, and lets
you navigate a multi-day / hourly forecast. Weather data comes from the free
[Open-Meteo](https://open-meteo.com) API (no API key required).

> ⚠️ **DELIBERATELY VULNERABLE.** Every dependency is pinned to a known-vulnerable version on
> purpose. This project is a **test fixture** for a CVE-extraction / SCA scanner. **Do not
> deploy it, do not expose it on a network, do not reuse these dependencies in production.**
> The full documented list of intentional CVEs is in [`CVE-MANIFEST.md`](CVE-MANIFEST.md).

## Features
- 🔍 Location search (Open-Meteo geocoding)
- 🌡️ Current conditions (temperature, humidity, wind, sky)
- 📅 7-day forecast with **◀ Prev / Next ▶** day navigation
- 🕐 Hourly detail for the selected day
- ⭐ Favorites (XML) and recent-search history (H2)

## Requirements
- JDK 17+ (developed against JDK 19 / Maven 3.9.2)
- Maven 3.9+
- Internet access (for Open-Meteo)

## Build
```
mvn clean package
```
Produces:
- `target/weather-app.jar` (thin)
- `target/weather-app-1.0.0-all.jar` (fat / shaded — all dependencies bundled, scannable as one artifact)

## Run
```
java -jar target/weather-app-1.0.0-all.jar
```
The app runs and the GUI works as-is on JDK 17+. The **favorites** feature uses XStream 1.4.5,
which needs reflective access on JDK 17+ (otherwise saving/loading favorites is silently disabled
and logged — the rest of the app is unaffected). To enable favorites, grant the opens:
```
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.text=ALL-UNNAMED \
     --add-opens java.desktop/java.awt.font=ALL-UNNAMED \
     -jar target/weather-app-1.0.0-all.jar
```

## Map: dependency → where it is used
| Dependency (vulnerable) | Used by |
|--------------------------|---------|
| Log4j 2.14.1 | logging everywhere |
| Jackson Databind 2.9.8 | `api/ForecastClient` (forecast JSON) |
| Gson 2.8.5 | `api/GeocodingClient` (geocoding JSON) |
| Spring 5.3.16 (`RestTemplate`) | `api/GeocodingClient` (HTTP) |
| Apache HttpClient 4.5.12 | `api/ForecastClient` (HTTP) |
| SnakeYAML 1.30 | `config/ConfigLoader` (`config.yaml`) |
| Commons Text 1.9 | `util/TemplateRenderer` (UI strings) |
| Commons Collections 3.2.1 | `util/ForecastCache` (LRU map) |
| Guava 24.1 | `util/ForecastCache` (temp dir, helpers) |
| BouncyCastle 1.64 | `util/CacheKeyHasher` (SHA-256) |
| H2 1.4.199 | `persistence/HistoryDao` (search history) |
| XStream 1.4.5 | `persistence/FavoritesStore` (XML) |

## Scanner ground truth
```
mvn dependency:tree -DoutputFile=deptree.txt
```
See [`CVE-MANIFEST.md`](CVE-MANIFEST.md) for the per-CVE table.

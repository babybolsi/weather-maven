package com.example.weather.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the recent search history in an embedded H2 1.4.199 database
 * (intentionally vulnerable: CVE-2022-23221, CVE-2021-23463).
 */
public class HistoryDao {

    private static final Logger LOG = LogManager.getLogger(HistoryDao.class);
    private static final String JDBC_URL = "jdbc:h2:./data/weather-history;AUTO_SERVER=TRUE";

    public HistoryDao() {
        try (Connection conn = open(); Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS search_history ("
                    + "id IDENTITY PRIMARY KEY, "
                    + "query VARCHAR(255), "
                    + "searched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            LOG.info("History store ready at {}", JDBC_URL);
        } catch (Exception e) {
            LOG.error("Failed to initialise H2 history store", e);
        }
    }

    private Connection open() throws Exception {
        return DriverManager.getConnection(JDBC_URL, "sa", "");
    }

    public void record(String query) {
        try (Connection conn = open();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO search_history(query) VALUES (?)")) {
            ps.setString(1, query);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error("Failed to record search '{}'", query, e);
        }
    }

    public List<String> recent(int limit) {
        List<String> out = new ArrayList<>();
        try (Connection conn = open();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT query FROM search_history ORDER BY searched_at DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to read search history", e);
        }
        return out;
    }
}

package com.example.weather;

import com.example.weather.config.ConfigLoader;
import com.example.weather.model.Config;
import com.example.weather.ui.MainWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Weather App.
 *
 * <p>WARNING: this application is a DELIBERATELY VULNERABLE test fixture. Its dependencies are
 * pinned to known-vulnerable versions on purpose so that a CVE-extraction scanner can be tested
 * against it. Do not deploy or expose it. See CVE-MANIFEST.md.
 */
public final class App {

    private static final Logger LOG = LogManager.getLogger(App.class);

    private App() {
    }

    public static void main(String[] args) {
        LOG.info("Starting Weather App (vulnerable test fixture)");
        Config config = ConfigLoader.load();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOG.warn("Could not set system look and feel", e);
            }
            new MainWindow(config).setVisible(true);
        });
    }
}

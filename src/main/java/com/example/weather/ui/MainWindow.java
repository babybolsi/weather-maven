package com.example.weather.ui;

import com.example.weather.api.WeatherService;
import com.example.weather.model.Config;
import com.example.weather.model.Location;
import com.example.weather.model.WeatherResult;
import com.example.weather.persistence.FavoritesStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.util.List;

/** Main application window. */
public class MainWindow extends JFrame {

    private static final Logger LOG = LogManager.getLogger(MainWindow.class);

    private final Config config;
    private final WeatherService service;
    private final FavoritesStore favoritesStore = new FavoritesStore();

    private final CurrentPanel currentPanel = new CurrentPanel();
    private final HourlyPanel hourlyPanel = new HourlyPanel();
    private final ForecastPanel forecastPanel = new ForecastPanel(hourlyPanel::setDay);
    private final JLabel statusBar = new JLabel(" ");
    private final SearchBar searchBar = new SearchBar(this::onSearch);

    private Location currentLocation;

    public MainWindow(Config config) {
        super("Weather App");
        this.config = config;
        this.service = new WeatherService(config);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(640, 720);
        setLocationRelativeTo(null);
        buildLayout();

        // Load the default city on startup.
        searchBar.setQuery(config.getDefaultCity());
        onSearch(config.getDefaultCity());
    }

    private void buildLayout() {
        JButton favButton = new JButton("★ Salva preferito");
        favButton.addActionListener(e -> saveFavorite());

        JPanel top = new JPanel(new BorderLayout());
        top.add(searchBar, BorderLayout.WEST);
        top.add(favButton, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        center.add(currentPanel, BorderLayout.NORTH);
        center.add(forecastPanel, BorderLayout.CENTER);
        center.add(hourlyPanel, BorderLayout.SOUTH);

        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void onSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        statusBar.setText("Ricerca di \"" + query.trim() + "\"...");
        new SwingWorker<List<Location>, Void>() {
            @Override
            protected List<Location> doInBackground() {
                return service.search(query);
            }

            @Override
            protected void done() {
                try {
                    List<Location> results = get();
                    if (results.isEmpty()) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Nessuna località trovata per \"" + query + "\".");
                        return;
                    }
                    Location chosen = results.size() == 1 ? results.get(0) : chooseLocation(results);
                    if (chosen != null) {
                        loadForecast(chosen);
                    }
                } catch (Exception e) {
                    showError("Ricerca località fallita", e);
                }
                updateStatusBar();
            }
        }.execute();
    }

    private Location chooseLocation(List<Location> results) {
        return (Location) JOptionPane.showInputDialog(this,
                "Seleziona la località:", "Risultati ricerca",
                JOptionPane.QUESTION_MESSAGE, null,
                results.toArray(), results.get(0));
    }

    private void loadForecast(Location location) {
        statusBar.setText("Caricamento previsioni per " + location + "...");
        new SwingWorker<WeatherResult, Void>() {
            @Override
            protected WeatherResult doInBackground() throws Exception {
                return service.forecastFor(location);
            }

            @Override
            protected void done() {
                try {
                    WeatherResult result = get();
                    currentLocation = result.getLocation();
                    currentPanel.update(result.getLocation(), result.getCurrent());
                    forecastPanel.setDays(result.getDays());
                } catch (Exception e) {
                    showError("Caricamento previsioni fallito", e);
                }
                updateStatusBar();
            }
        }.execute();
    }

    private void saveFavorite() {
        if (currentLocation == null) {
            JOptionPane.showMessageDialog(this, "Nessuna località selezionata.");
            return;
        }
        List<Location> favorites = favoritesStore.load();
        favorites.add(currentLocation);
        favoritesStore.save(favorites);
        JOptionPane.showMessageDialog(this, "Salvato tra i preferiti: " + currentLocation);
    }

    private void updateStatusBar() {
        List<String> recent = service.recentSearches(5);
        statusBar.setText(recent.isEmpty() ? " " : "Ricerche recenti: " + String.join(", ", recent));
    }

    private void showError(String message, Exception e) {
        LOG.error(message, e);
        JOptionPane.showMessageDialog(this, message + ":\n" + e.getMessage(),
                "Errore", JOptionPane.ERROR_MESSAGE);
    }
}

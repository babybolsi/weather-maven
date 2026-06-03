package com.example.weather.ui;

import com.example.weather.model.CurrentConditions;
import com.example.weather.model.Location;
import com.example.weather.util.TemplateRenderer;
import com.example.weather.util.WeatherCodes;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/** Shows the current conditions for the selected location. */
public class CurrentPanel extends JPanel {

    private final JLabel title = new JLabel("Cerca una località per iniziare");
    private final JLabel icon = new JLabel(" ");
    private final JLabel temperature = new JLabel(" ");
    private final JLabel details = new JLabel(" ");

    public CurrentPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        icon.setFont(icon.getFont().deriveFont(48f));
        temperature.setFont(temperature.getFont().deriveFont(Font.BOLD, 32f));

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);
        temperature.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(title);
        add(icon);
        add(temperature);
        add(details);
    }

    public void update(Location location, CurrentConditions current) {
        // Title built via Commons Text StringSubstitutor (Text4Shell code path).
        Map<String, String> values = new HashMap<>();
        values.put("city", location.toString());
        title.setText(TemplateRenderer.render("Meteo attuale per ${city}", values));

        int code = current.getWeatherCode();
        icon.setText(WeatherCodes.icon(code));
        temperature.setText(String.format("%.1f°", current.getTemperature()));
        details.setText(String.format("<html>%s &nbsp;•&nbsp; Umidità %d%% &nbsp;•&nbsp; Vento %.1f</html>",
                WeatherCodes.label(code), current.getHumidity(), current.getWindSpeed()));
    }
}

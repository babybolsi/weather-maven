package com.example.weather.ui;

import com.example.weather.model.DailyForecast;
import com.example.weather.util.WeatherCodes;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/** Multi-day forecast with Prev/Next day navigation. */
public class ForecastPanel extends JPanel {

    private static final Locale IT = Locale.ITALIAN;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEEE d MMMM", IT);

    private final Consumer<DailyForecast> onDaySelected;
    private List<DailyForecast> days = new ArrayList<>();
    private int index = 0;

    private final JButton prev = new JButton("◀ Giorno prec.");
    private final JButton next = new JButton("Giorno succ. ▶");
    private final JLabel position = new JLabel("", SwingConstants.CENTER);
    private final JLabel dayDate = new JLabel("", SwingConstants.CENTER);
    private final JLabel dayIcon = new JLabel("", SwingConstants.CENTER);
    private final JLabel dayTemps = new JLabel("", SwingConstants.CENTER);
    private final JLabel dayLabel = new JLabel("", SwingConstants.CENTER);

    public ForecastPanel(Consumer<DailyForecast> onDaySelected) {
        this.onDaySelected = onDaySelected;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Previsioni"));

        JPanel nav = new JPanel(new BorderLayout());
        nav.add(prev, BorderLayout.WEST);
        nav.add(position, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        dayDate.setFont(dayDate.getFont().deriveFont(Font.BOLD, 15f));
        dayIcon.setFont(dayIcon.getFont().deriveFont(40f));
        dayTemps.setFont(dayTemps.getFont().deriveFont(Font.BOLD, 18f));
        for (JLabel l : new JLabel[]{dayDate, dayIcon, dayTemps, dayLabel}) {
            l.setAlignmentX(CENTER_ALIGNMENT);
            center.add(l);
        }

        add(nav, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        prev.addActionListener(e -> move(-1));
        next.addActionListener(e -> move(1));
        refresh();
    }

    public void setDays(List<DailyForecast> days) {
        this.days = days != null ? days : new ArrayList<>();
        this.index = 0;
        refresh();
    }

    private void move(int delta) {
        int target = index + delta;
        if (target >= 0 && target < days.size()) {
            index = target;
            refresh();
        }
    }

    private void refresh() {
        boolean has = !days.isEmpty();
        prev.setEnabled(has && index > 0);
        next.setEnabled(has && index < days.size() - 1);

        if (!has) {
            position.setText("Nessuna previsione");
            dayDate.setText(" ");
            dayIcon.setText(" ");
            dayTemps.setText(" ");
            dayLabel.setText(" ");
            onDaySelected.accept(null);
            return;
        }

        DailyForecast d = days.get(index);
        position.setText(String.format("Giorno %d / %d", index + 1, days.size()));
        String date = d.getDate().format(DATE);
        dayDate.setText(date.substring(0, 1).toUpperCase(IT) + date.substring(1));
        dayIcon.setText(WeatherCodes.icon(d.getWeatherCode()));
        dayTemps.setText(String.format("Max %.1f°  /  Min %.1f°", d.getTempMax(), d.getTempMin()));
        dayLabel.setText(WeatherCodes.label(d.getWeatherCode()));
        onDaySelected.accept(d);
    }
}

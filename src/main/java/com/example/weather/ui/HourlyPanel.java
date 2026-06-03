package com.example.weather.ui;

import com.example.weather.model.DailyForecast;
import com.example.weather.model.HourlyForecast;
import com.example.weather.util.WeatherCodes;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.time.format.DateTimeFormatter;

/** Hourly breakdown of the currently selected day. */
public class HourlyPanel extends JScrollPane {

    private static final DateTimeFormatter HOUR = DateTimeFormatter.ofPattern("HH:mm");

    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{"Ora", "Temp", "Condizioni"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    public HourlyPanel() {
        super(new JTable());
        JTable table = (JTable) getViewport().getView();
        table.setModel(model);
        table.setRowHeight(24);
        setBorder(BorderFactory.createTitledBorder("Dettaglio orario"));
    }

    public void setDay(DailyForecast day) {
        model.setRowCount(0);
        if (day == null) {
            return;
        }
        for (HourlyForecast h : day.getHours()) {
            model.addRow(new Object[]{
                    h.getTime().format(HOUR),
                    String.format("%.1f°", h.getTemperature()),
                    WeatherCodes.icon(h.getWeatherCode()) + " " + WeatherCodes.label(h.getWeatherCode())
            });
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, 220);
    }
}

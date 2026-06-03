package com.example.weather.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/** Top search bar: a text field plus a "Cerca" button. */
public class SearchBar extends JPanel {

    private final JTextField field = new JTextField(24);

    public SearchBar(Consumer<String> onSearch) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton button = new JButton("Cerca");
        add(new JLabel("Località:"));
        add(field);
        add(button);

        ActionListener action = e -> onSearch.accept(field.getText());
        button.addActionListener(action);
        field.addActionListener(action); // Enter key triggers search too.
    }

    public void setQuery(String text) {
        field.setText(text);
    }

    public String getQuery() {
        return field.getText();
    }
}

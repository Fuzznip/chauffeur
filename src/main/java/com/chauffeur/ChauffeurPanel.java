package com.chauffeur;

import com.chauffeur.utils.HttpClient;
import com.chauffeur.models.EventConfig;
import net.runelite.api.Client;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

public class ChauffeurPanel extends PluginPanel {
    private final HttpClient httpClient;
    private final JLabel contentLabel;
    private final JTextField eventCodeField;
    private final JButton confirmButton;

    @Inject
    public ChauffeurPanel(Client client, ChauffeurPlugin plugin, ChauffeurConfig config, HttpClient httpClient) {
        super(false);
        this.httpClient = httpClient;

        // Create input field and button
        eventCodeField = new JTextField(15);
        eventCodeField.addActionListener(e -> fetchEventConfig());
        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> fetchEventConfig());

        // Create content label
        contentLabel = new JLabel();
        contentLabel.setHorizontalAlignment(SwingConstants.LEFT);
        contentLabel.setVerticalAlignment(SwingConstants.TOP);
    }

    public void init() {
        // Create input panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(eventCodeField);
        inputPanel.add(confirmButton);
        add(inputPanel);

        // Create scroll pane for content
        JScrollPane scrollPane = new JScrollPane(contentLabel);
        scrollPane.setPreferredSize(new Dimension(220, 400));
        add(scrollPane);

        fetchEventConfig();
        revalidate();
    }

    private void fetchEventConfig() {
        String eventCode = eventCodeField.getText().trim();
        if (eventCode.isEmpty()) {
            contentLabel.setText("Please enter an event code");
            return;
        }
        String url = String.format("http://127.0.0.1:8080/event?event_code=%s", eventCode);

        EventConfig eventConfig = httpClient.getRequest(url, EventConfig.class);
        String displayText = eventConfig != null ?
            String.format("<html><div style='width: 170px;'>" +
                "Event: %s<br/>" +
                "Description: %s<br/>" +
                "Code: %s<br/><br/>" +
                "Image Whitelist:<br/>%s<br/><br/>" +
                "No Image Whitelist:<br/>%s" +
                "</div></html>",
                eventConfig.getEvent(),
                eventConfig.getDescription(),
                eventConfig.getEventCode(),
                String.join("<br/>", eventConfig.getImageWhitelist()),
                String.join("<br/>", eventConfig.getNoImageWhitelist())) :
            "Failed to load data";

        contentLabel.setText(displayText);
    }

    public void shutdown() {
        removeAll();
    }
}
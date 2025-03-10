package com.chauffeur;

import com.chauffeur.services.EventService;
import com.chauffeur.utils.HttpClient;
import com.chauffeur.models.EventConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

@Slf4j
public class ChauffeurPanel extends PluginPanel {
    private final HttpClient httpClient;
    private final JLabel contentLabel;
    private final JTextField eventCodeField;
    private final JButton confirmButton;
    private final ChauffeurConfig config;
    private final ConfigManager configManager;
    private final EventService eventService;

    @Inject
    public ChauffeurPanel(Client client, ChauffeurPlugin plugin, ChauffeurConfig config, HttpClient httpClient, ConfigManager configManager, EventService eventService) {
        super(false);
        this.httpClient = httpClient;
        this.config = config;
        this.configManager = configManager;
        this.eventService = eventService;

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

        String savedEventCode = configManager.getConfiguration("chauffeur", "eventCode");
        if(savedEventCode != null) {
            eventCodeField.setText(savedEventCode);
            fetchEventConfig();
        }

        revalidate();
    }

    private String ensureHttpProtocol(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }

    private void fetchEventConfig() {
        String eventCode = eventCodeField.getText().trim();
        if (eventCode.isEmpty()) {
            contentLabel.setText("Please enter an event code");
            return;
        }

        configManager.setConfiguration("chauffeur", "eventCode", eventCode);

        String configUrl = ensureHttpProtocol(config.serverUrl());
        String url = String.format("%s/event?event_code=%s", configUrl, eventCode);
        log.info("Fetching event config from {}", url);

        EventConfig fetchedEventConfig = httpClient.getRequest(url, EventConfig.class);
        if (fetchedEventConfig == null) {
            log.error("Failed to fetch event config from {}", url);
            contentLabel.setText("Failed to load data");
            return;
        }

        log.info("Fetched event config: {}", fetchedEventConfig);
        eventService.setEventConfig(fetchedEventConfig);

        String displayText = String.format("<html><div style='width: 170px;'>" +
            "Event: %s<br/>" +
            "Description: %s<br/>" +
            "Code: %s<br/><br/>" +
            "Image Whitelist:<br/>%s<br/><br/>" +
            "No Image Whitelist:<br/>%s" +
            "</div></html>",
            fetchedEventConfig.getEvent(),
            fetchedEventConfig.getDescription(),
            fetchedEventConfig.getEventCode(),
            fetchedEventConfig.getImageWhitelist().stream()
                .map(item -> item.getSource() != null ?
                    item.getDrop() + " - " + item.getSource() :
                    item.getDrop())
                .collect(Collectors.joining("<br/>")),
            fetchedEventConfig.getNoImageWhitelist().stream()
                .map(item -> item.getSource() != null ?
                    item.getDrop() + " - " + item.getSource() :
                    item.getDrop())
                .collect(Collectors.joining("<br/>")));

        contentLabel.setText(displayText);
    }

    public void shutdown() {
        removeAll();
    }
}
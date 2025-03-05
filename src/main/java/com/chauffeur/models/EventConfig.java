package com.chauffeur.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EventConfig {
    private String event;
    private String description;
    @SerializedName("event_code")
    private String eventCode;
    @SerializedName("image_whitelist")
    private List<String> imageWhitelist;
    @SerializedName("no_image_whitelist")
    private List<String> noImageWhitelist;

    // Getters
    public String getEvent() { return event; }
    public String getDescription() { return description; }
    public String getEventCode() { return eventCode; }
    public List<String> getImageWhitelist() { return imageWhitelist; }
    public List<String> getNoImageWhitelist() { return noImageWhitelist; }
}
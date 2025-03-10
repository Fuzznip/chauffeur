package com.chauffeur.models;

import com.google.gson.annotations.SerializedName;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class EventConfig {
    private String event;
    private String description;
    @SerializedName("event_code")
    private String eventCode;
    @SerializedName("event_type")
    private String eventType;
    @SerializedName("image_whitelist")
    private List<WhitelistItem> imageWhitelist;
    @SerializedName("no_image_whitelist")
    private List<WhitelistItem> noImageWhitelist;

    // Getters
    public String getEvent() { return event; }
    public String getDescription() { return description; }
    public String getEventCode() { return eventCode; }
    public String getEventType() { return eventType; }
    public List<WhitelistItem> getImageWhitelist() { return imageWhitelist; }
    public List<WhitelistItem> getNoImageWhitelist() { return noImageWhitelist; }
}
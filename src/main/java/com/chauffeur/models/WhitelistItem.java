package com.chauffeur.models;

import com.google.gson.annotations.SerializedName;

public class WhitelistItem {
    private String drop;
    private String source;

    public String getDrop() { return drop; }
    public void setDrop(String drop) { this.drop = drop; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
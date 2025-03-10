package com.chauffeur.models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class WhitelistItem {
    private String drop;
    private String source;

    public String getDrop() { return drop; }
    public void setDrop(String drop) { this.drop = drop; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof WhitelistItem)) {
            return false;
        }
        WhitelistItem item = (WhitelistItem) obj;
        return Objects.equals(drop, item.drop) && Objects.equals(source, item.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(drop, source);
    }
}
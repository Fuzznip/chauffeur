package com.chauffeur.models.payloads;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Payload<T> {
    public String playerName;
    public T data;

    public Image image;

    public static class Image {
        public String url;
    }
}
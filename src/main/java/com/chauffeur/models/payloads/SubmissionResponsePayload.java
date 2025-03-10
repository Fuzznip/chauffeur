package com.chauffeur.models.payloads;

public class SubmissionResponsePayload {
    public String message;
    public Notification notification;
    public static class Notification {
        public String title;
        public String body;
    }
}

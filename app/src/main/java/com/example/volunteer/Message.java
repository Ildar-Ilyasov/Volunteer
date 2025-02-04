package com.example.volunteer;

public class Message {
    private String userId;
    private String text;
    private long timestamp;  // Изменено с String на long

    public Message() {
    }

    public Message(String userId, String text, long timestamp) {
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {  // Изменено
        return timestamp;
    }

    public void setTimestamp(long timestamp) {  // Изменено
        this.timestamp = timestamp;
    }
}




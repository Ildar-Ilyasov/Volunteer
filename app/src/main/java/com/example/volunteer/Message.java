package com.example.volunteer;

public class Message {
    private String userId;
    private String userName;
    private String text;
    private long timestamp;

    public Message() {} // для Firebase

    public Message(String userId, String text, long timestamp) {
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Message(String userId, String userName, String text, long timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName != null ? userName : userId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

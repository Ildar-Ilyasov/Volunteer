package com.example.volunteer;

public class UserEvent {
    private String userId;
    private String eventId;

    public UserEvent(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    // Геттеры и сеттеры
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}


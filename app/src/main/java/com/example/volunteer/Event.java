package com.example.volunteer;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Event {
    private String id;          // Уникальный идентификатор мероприятия
    private String title;       // Название мероприятия
    private String description; // Описание мероприятия
    private String date;        // Дата проведения
    private String location;    // Место проведения
    private String organizer;   // Организатор мероприятия

    // Конструктор по умолчанию (обязателен для Firebase)
    public Event() {}

    // Конструктор с параметрами
    public Event(String title, String description, String date, String location, String organizer) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.organizer = organizer;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", organizer='" + organizer + '\'' +
                '}';
    }
}

package com.example.volunteer;

public class News {
    private String id;
    private String title;
    private String content;
    private String ImageUrl;
    private String publication_date;

    public News() {
    }

    public News(String id, String title, String content, String ImageUrl, String publication_date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.ImageUrl = ImageUrl;
        this.publication_date = publication_date;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublication_Date() {
        return publication_date;
    }

    public void setPublication_Date(String publication_Date) {
        this.publication_date = publication_date;
    }
}


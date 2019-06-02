package com.koukou.it.bean;

public class Note {
    private String id;
    private String title;
    private String content;
    private String imagePath;
    private String userId;

    public Note(String id, String title, String content, String imagePath, String userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.userId = userId;
    }

    public Note(String title, String content, String imagePath, String userId) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.userId = userId;
    }

    public Note() {
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Note{imagePath:" + imagePath + " userId: " + userId + "}";
    }
}

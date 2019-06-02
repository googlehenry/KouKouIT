package com.koukou.it.bean;

public class User {
    private String id;
    private String name;
    private String password;
    private String photopath;

    public User() {
    }

    public User(String id, String name, String password, String photopath) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.photopath = photopath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotopath() {
        return photopath;
    }

    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }
}

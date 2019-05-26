package com.koukou.it.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

public class Account {
    private String id;
    private String imagePath;
    private String category;
    private BigDecimal amount;
    private String date;
    private String userId;
    private Time time;


    public Account(String id, String imagePath, String category, BigDecimal amount, String date, String userId) {
        this.id = id;
        this.imagePath = imagePath;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.userId = userId;
    }

    public Account() {
    }

    public Account(String imagePath, String category, BigDecimal amount, String date, String userId) {
        this.imagePath = imagePath;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

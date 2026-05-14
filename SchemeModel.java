package com.example.farmconnect.activities;

public class SchemeModel {

    String title;
    String description;
    String url;

    public SchemeModel(String title, String description, String url) {
        this.title       = title;
        this.description = description;
        this.url         = url;
    }

    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getUrl()         { return url; }
}
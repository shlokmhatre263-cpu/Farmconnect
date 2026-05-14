package com.example.farmconnect.community;

public class CommunityPost {

    public String userName;
    public String message;
    public String date;
    public String imageBase64;   // 🔥 Changed from imageUrl
    public int likes;
    public int comments;

    public CommunityPost() {}

    public CommunityPost(String userName,
                         String message,
                         String date,
                         String imageBase64,
                         int likes,
                         int comments) {

        this.userName = userName;
        this.message = message;
        this.date = date;
        this.imageBase64 = imageBase64;
        this.likes = likes;
        this.comments = comments;
    }
}
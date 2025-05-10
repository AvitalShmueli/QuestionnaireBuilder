package com.example.questionnairebuilder.models;

public class User {
    public static final String USERNAME = "USERNAME";
    private String uid;
    private String username;
    private String email;
    private String profileImageUrl;

    public User() {
    }

    public User(String uid, String username, String email, String profileImageUrl) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public User setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this;
    }
}

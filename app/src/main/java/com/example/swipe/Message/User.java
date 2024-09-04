package com.example.swipe.Message;

public class User {
    private String userId;
    private String username;
    private String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
